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

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Class that implements methods of {@link TourGuideClientService}
 *
 * @author Christine Duarte
 */
@Service
@Slf4j
public class TourGuideClientServiceImpl implements TourGuideClientService {

    public final Tracker tracker;
    private final MicroserviceUserGpsProxy microserviceUserGpsProxy;
    private final MicroserviceRewardsProxy microserviceRewardsProxy;
    private final InternalUserMapDAO internalUserMapDAO;
    private final TourGuideClientRewardsServiceImpl tourGuideClientRewardsServiceImpl;
    private final MicroserviceTripPricerProxy microserviceTripPricerProxy;
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

    public CompletableFuture<VisitedLocation> trackUserLocation(User user) {
        final ExecutorService executorService = Executors.newFixedThreadPool(1600);
        CompletableFuture<VisitedLocation> visitedLocationFuture = null;
        try {
            Locale.setDefault(new Locale("en", "US"));

            visitedLocationFuture = CompletableFuture.supplyAsync(
                    () -> microserviceUserGpsProxy.trackUserLocation(user.getUserId()), executorService);
            visitedLocationFuture.thenCompose(visitedLocation ->
                    CompletableFuture.runAsync(() -> {
                        addToVisitedLocations(visitedLocation, user);
                        tourGuideClientRewardsServiceImpl.calculateRewards(user);
                    }, executorService)
            );
        } catch (NumberFormatException ex) {
            log.debug("NumberFormatException: " + ex.getMessage());
        }
        log.debug("Service - user location tracked for username: " + user.getUserName());

        return visitedLocationFuture;
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
                trackUserLocation(user).join();

        log.debug("Service - get user location for user: " + userName);

        return visitedLocation;
    }

    @Override
    public List<UserCurrentLocation> getAllCurrentLocations() {
        List<User> users = internalUserMapDAO.getAllUsers();
        log.debug("Service - Current location for all users got");

        return users.stream()
                .map(u -> new UserCurrentLocation(u.getUserId(), u.getVisitedLocations().get(u.getVisitedLocations().size() - 1).getLocation()))
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
        List<Provider> providers = microserviceTripPricerProxy.getProviders(
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
        List<Attraction> attractions = microserviceUserGpsProxy.getAttractionsByAverageDistance(
                visitedLocation.getLocation().getLatitude(), visitedLocation.getLocation().getLongitude());

        List<NearByAttraction> nearByAttractionsList = attractions.stream()
                .sorted((o1, o2) -> {
                    Double distanceO1 = tourGuideClientRewardsServiceImpl.getDistance(o1, visitedLocation.getLocation());
                    Double distanceO2 = tourGuideClientRewardsServiceImpl.getDistance(o2, visitedLocation.getLocation());

                    return distanceO1 == distanceO2 ? 0 : distanceO1 > distanceO2 ? 1 : -1;
                })
                .limit(5)
                .map(attraction -> buildNearByAttraction(visitedLocation, attraction))
                .collect(Collectors.toList());

        log.debug("Service - Attractions near of position of user: " + visitedLocation.getUserId());
        return nearByAttractionsList;
    }

    private NearByAttraction buildNearByAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return new NearByAttraction(attraction.getAttractionName(),
                new Location(attraction.getLatitude(), attraction.getLongitude()),
                visitedLocation.getLocation(),
                (int) tourGuideClientRewardsServiceImpl.getDistance(attraction, visitedLocation.getLocation()),
                microserviceRewardsProxy.getRewardsPoints(attraction.getAttractionId(), visitedLocation.getUserId()));
    }

    public void addToVisitedLocations(VisitedLocation visitedLocation, User user) {
        CopyOnWriteArrayList<VisitedLocation> visitedLocations = user.getVisitedLocations();
        visitedLocations.add(visitedLocation);
        user.setVisitedLocations(visitedLocations);

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
