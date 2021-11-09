package com.tripMaster.tourguideclient.service;

import com.tripMaster.tourguideclient.DAO.InternalUserMapDAO;
import com.tripMaster.tourguideclient.exception.UserNotFoundException;
import com.tripMaster.tourguideclient.exception.UserRewardsNotFoundException;
import com.tripMaster.tourguideclient.model.*;
import com.tripMaster.tourguideclient.proxies.MicroserviceTripPricerProxy;
import com.tripMaster.tourguideclient.proxies.MicroserviceUserGpsProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Slf4j
public class TourGuideClientServiceImpl implements TourGuideClientService {

    private MicroserviceUserGpsProxy microserviceUserGpsProxy;
    private InternalUserMapDAO internalUserMapDAO;
    private TourGuideClientRewardsServiceImpl tourGuideClientRewardsServiceImpl;
    private MicroserviceTripPricerProxy microserviceTripPricerProxy;

    @Autowired
    public TourGuideClientServiceImpl(MicroserviceUserGpsProxy microserviceUserGpsProxy, InternalUserMapDAO internalUserMapDAO, TourGuideClientRewardsServiceImpl tourGuideClientRewardsServiceImpl, MicroserviceTripPricerProxy microserviceTripPricerProxy) {
        this.microserviceUserGpsProxy = microserviceUserGpsProxy;
        this.internalUserMapDAO = internalUserMapDAO;
        this.tourGuideClientRewardsServiceImpl = tourGuideClientRewardsServiceImpl;
        this.microserviceTripPricerProxy = microserviceTripPricerProxy;
    }

    public VisitedLocation trackUserLocation(User user) {
        Locale.setDefault(new Locale("en", "US"));
        VisitedLocation visitedLocation = microserviceUserGpsProxy.trackUserLocation(user.getUserId());
        addToVisitedLocations(visitedLocation, user);
        tourGuideClientRewardsServiceImpl.calculateRewards(user);
        log.debug("Service - user location tracked for username: " + user.getUserName());
        return visitedLocation;
    }

    @Override
    public VisitedLocation getUserLocation(String userName) {
        User user = internalUserMapDAO.getUser(userName);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        List<VisitedLocation> visitedLocations = user.getVisitedLocations();

        VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
                user.getVisitedLocations().get(visitedLocations.size() - 1) :
                trackUserLocation(user);

        log.debug("Service - get user location for user: " + user.getUserName());
        return visitedLocation;
    }

    @Override
    public List<Provider> getTripDeals(String userName) {
        User user = internalUserMapDAO.getUser(userName);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        List<UserReward> userRewards = user.getUserRewards();
        if (userRewards.isEmpty()) {
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
        return providers;
    }

    @Override
    public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
        List<Attraction> nearbyAttractions = new ArrayList<>();
        List<Double> distances = new ArrayList<>();

        for (Attraction attraction : microserviceUserGpsProxy.getAttractions()) {
            if (tourGuideClientRewardsServiceImpl.isWithinAttractionProximity(attraction, visitedLocation.getLocation())) {
                nearbyAttractions.add(attraction);
            } else {
                distances.add(tourGuideClientRewardsServiceImpl.getDistance(attraction, visitedLocation.getLocation()));
            }
        }
        //todo a verifier ajouter par moi
//        tourGuideClientRewardsServiceImpl.setAttractionProximityRange((int)getAverageDistanceByAttraction(distances));

        return nearbyAttractions;
    }

//    private UUID getAttractionId(User user) {
//        List<UserReward> userRewards = user.getUserRewards();
//        if (userRewards.size() > 0) {
//            return userRewards.get(userRewards.size() - 1).getAttraction().getAttractionId();
//        }
////        return UUID.randomUUID();
//        return null;
//        //todo a revoir pourquoi mes rewards sont null ts le tps
//    }

    private void addToVisitedLocations(VisitedLocation visitedLocation, User user) {
        List<VisitedLocation> visitedLocations = user.getVisitedLocations();
        visitedLocations.add(visitedLocation);
        log.debug("Service - Visited location added for user: " + user.getUserName());
    }

    private double getAverageDistanceByAttraction(List<Double> distances) {

        return distances.stream().mapToDouble(s -> s)
                .average()
                .orElse(0D);
    }


}
