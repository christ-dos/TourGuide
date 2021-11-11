package com.tripMaster.tourguideclient.service;

import com.tripMaster.tourguideclient.DAO.InternalUserMapDAO;
import com.tripMaster.tourguideclient.exception.UserNotFoundException;
import com.tripMaster.tourguideclient.exception.UserRewardsNotFoundException;
import com.tripMaster.tourguideclient.model.*;
import com.tripMaster.tourguideclient.proxies.MicroserviceRewardsProxy;
import com.tripMaster.tourguideclient.proxies.MicroserviceTripPricerProxy;
import com.tripMaster.tourguideclient.proxies.MicroserviceUserGpsProxy;
import com.tripMaster.tourguideclient.utils.Tracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TourGuideClientServiceImpl implements TourGuideClientService {
    public final Tracker tracker;
    private MicroserviceUserGpsProxy microserviceUserGpsProxy;
    private MicroserviceRewardsProxy microserviceRewardsProxy;
    private InternalUserMapDAO internalUserMapDAO;
    private TourGuideClientRewardsServiceImpl tourGuideClientRewardsServiceImpl;
    private MicroserviceTripPricerProxy microserviceTripPricerProxy;
    boolean testMode = true;

    @Autowired
    public TourGuideClientServiceImpl(MicroserviceRewardsProxy microserviceRewardsProxy, MicroserviceUserGpsProxy microserviceUserGpsProxy, InternalUserMapDAO internalUserMapDAO, TourGuideClientRewardsServiceImpl tourGuideClientRewardsServiceImpl, MicroserviceTripPricerProxy microserviceTripPricerProxy) {
        this.microserviceUserGpsProxy = microserviceUserGpsProxy;
        this.internalUserMapDAO = internalUserMapDAO;
        this.tourGuideClientRewardsServiceImpl = tourGuideClientRewardsServiceImpl;
        this.microserviceTripPricerProxy = microserviceTripPricerProxy;
        this.microserviceRewardsProxy = microserviceRewardsProxy;

        if (testMode) {
            log.info("TestMode enabled");
            log.debug("Initializing users");
            internalUserMapDAO.initializeInternalUsers();
            log.debug("Finished initializing users");
        }
        tracker = new Tracker(this);
        addShutDownHook();

    }

    public VisitedLocation trackUserLocation(User user) {
        Locale.setDefault(new Locale("en", "US"));
        VisitedLocation visitedLocation = microserviceUserGpsProxy.trackUserLocation(user.getUserId());
        addToVisitedLocations(visitedLocation, user);
        tourGuideClientRewardsServiceImpl.calculateRewards(user);

        log.debug("Service - user location tracked for username: " + user.getUserName());
        return visitedLocation;
    }

    public User getUser(String userName) {
        User user = internalUserMapDAO.getUser(userName);
        if (user == null) {
            log.error("Service - user not found");
            throw new UserNotFoundException("User not found");
        }
        log.debug(("Service - user found: " + userName));
        return user;
    }

    public List<User> getAllUsers() {
        return internalUserMapDAO.getAllUsers();
    }

    public void addUser(User user) {
        internalUserMapDAO.addUser(user);
    }

    @Override
    public VisitedLocation getUserLocation(String userName) {
        User user = internalUserMapDAO.getUser(userName);
        if (user == null) {
            log.error("Service - user not found");
            throw new UserNotFoundException("User not found");
        }
        List<VisitedLocation> visitedLocations = user.getVisitedLocations();
        VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
                user.getVisitedLocations().get(visitedLocations.size() - 1) :
                trackUserLocation(user);

        log.debug("Service - get user location for user: " + userName);
        return visitedLocation;
    }

    @Override
    public List<UserCurrentLocation> getAllCurrentLocations() {
        List<User> users = internalUserMapDAO.getAllUsers();

        log.debug("Service - current location for all users getted");
        return  users.stream().map(u ->
                        new UserCurrentLocation(u.getUserId(), u.getVisitedLocations().get(u.getVisitedLocations().size() - 1).getLocation()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Provider> getTripDeals(String userName) {
        User user = internalUserMapDAO.getUser(userName);
        if (user == null) {
            log.error("Service - user not found");
            throw new UserNotFoundException("User not found");
        }
        List<UserReward> userRewards = user.getUserRewards();
        if (userRewards.isEmpty()) {
            log.error("Service - userRewards not found");
            throw new UserRewardsNotFoundException("User Rewards not found");
        }
        UUID attractionId = userRewards.get(userRewards.size() - 1).getAttraction().getAttractionId();

        int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
        List<Provider> providers =
                microserviceTripPricerProxy.getProviders(
                        internalUserMapDAO.getTripPricerApiKey(),
                        attractionId,
                        user.getUserPreferences().getNumberOfAdults(),
                        user.getUserPreferences().getNumberOfChildren(),
                        user.getUserPreferences().getTripDuration(),
                        cumulativeRewardPoints);
        user.setTripDeals(providers);
        log.debug("Service - list of providers getted for user: " + userName);
        return providers;
    }

    @Override
    public List<NearByAttraction> getNearByAttractions(VisitedLocation visitedLocation) {
        List<Attraction> nearbyAttractions = new ArrayList<>();
        List<Double> distances = new ArrayList<>();
        List<Attraction> attractions = microserviceUserGpsProxy.getAttractions();

        for (Attraction attraction : attractions) {
            if (tourGuideClientRewardsServiceImpl.isWithinAttractionProximity(attraction, visitedLocation.getLocation())) {
                nearbyAttractions.add(attraction);
                log.debug("Service - Attraction added in nearbyAttraction list,attraction with ID: " + attraction.getAttractionId());

            } else {
                distances.add(tourGuideClientRewardsServiceImpl.getDistance(attraction, visitedLocation.getLocation()));
                log.info("Service - Distance added in list distances attraction is far");
            }
        }
        double averageDistances = 0;
        List<NearByAttraction> nearbyAttractionsList = new ArrayList<>();
        if (nearbyAttractions.size() < 5) {
            averageDistances = getAverageDistanceByAttraction(distances);
            tourGuideClientRewardsServiceImpl.setAttractionProximityRange((int) averageDistances);

            nearbyAttractionsList = attractions.stream()
                    .filter(attraction -> tourGuideClientRewardsServiceImpl.isWithinAttractionProximity(attraction, visitedLocation.getLocation()))
                    .map(attraction ->
                            new NearByAttraction(attraction.getAttractionName(),
                                    new Location(attraction.getLatitude(), attraction.getLongitude()),
                                    visitedLocation.getLocation(),
                                    (int) tourGuideClientRewardsServiceImpl.getDistance(attraction, visitedLocation.getLocation()),
                                    microserviceRewardsProxy.getRewardsPoints(attraction.getAttractionId(), visitedLocation.getUserId())))
                    .sorted()
                    .limit(5)
                    .collect(Collectors.toList());
            return nearbyAttractionsList;
        }
        nearbyAttractionsList = nearbyAttractions.stream()
                .map(attraction ->
                        new NearByAttraction(attraction.getAttractionName(),
                                new Location(attraction.getLatitude(), attraction.getLongitude()),
                                visitedLocation.getLocation(),
                                (int) tourGuideClientRewardsServiceImpl.getDistance(attraction, visitedLocation.getLocation()),
                                microserviceRewardsProxy.getRewardsPoints(attraction.getAttractionId(), visitedLocation.getUserId())))
                .sorted()
                .limit(5)
                .collect(Collectors.toList());
        log.debug("Service - list of attractions near by position for user: " + visitedLocation.getUserId());
        return nearbyAttractionsList;
    }

    public void addToVisitedLocations(VisitedLocation visitedLocation, User user) {
        List<VisitedLocation> visitedLocations = user.getVisitedLocations();
        visitedLocations.add(visitedLocation);
        log.debug("Service - Visited location added for user: " + user.getUserName());
    }

    private double getAverageDistanceByAttraction(List<Double> distances) {
        log.debug("Service - Average calculated");
        return distances.stream().mapToDouble(s -> s)
                .average()
                .orElse(0D);
    }

    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                tracker.stopTracking();
                log.info("Service - Stop tracking");
            }
        });
    }
}
