package com.tripMaster.tourguideclient.service;

import com.tripMaster.tourguideclient.DAO.InternalUserMapDAO;
import com.tripMaster.tourguideclient.exception.UserNotFoundException;
import com.tripMaster.tourguideclient.model.*;
import com.tripMaster.tourguideclient.proxies.MicroserviceRewardsProxy;
import com.tripMaster.tourguideclient.proxies.MicroserviceUserGpsProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class TourGuideClientRewardsServiceImpl implements TourGuideClientRewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

    // proximity in miles
    private int defaultProximityBuffer = 10;
    private int proximityBuffer = defaultProximityBuffer;
    private int attractionProximityRange = 200;

    private MicroserviceRewardsProxy microserviceRewardsProxy;
    private MicroserviceUserGpsProxy microserviceUserGpsProxy;
    private InternalUserMapDAO internalUserMapDAO;

    @Autowired
    public TourGuideClientRewardsServiceImpl(MicroserviceRewardsProxy microserviceRewardsProxy, MicroserviceUserGpsProxy microserviceUserGpsProxy, InternalUserMapDAO internalUserMapDAO) {
        this.microserviceRewardsProxy = microserviceRewardsProxy;
        this.microserviceUserGpsProxy = microserviceUserGpsProxy;
        this.internalUserMapDAO = internalUserMapDAO;
    }


    public void setDefaultProximityBuffer(int defaultProximityBuffer) {
        this.defaultProximityBuffer = defaultProximityBuffer;
    }

    public void setProximityBuffer(int proximityBuffer) {
        this.proximityBuffer = proximityBuffer;
    }

    public void setAttractionProximityRange(int attractionProximityRange) {
        this.attractionProximityRange = attractionProximityRange;
    }

    @Override
    public void calculateRewards(User user) {
        final ExecutorService executorService = Executors.newFixedThreadPool(1000);

        List<VisitedLocation> userLocations = user.getVisitedLocations();
        CompletableFuture<List<Attraction>> attractions = CompletableFuture.supplyAsync(() -> microserviceUserGpsProxy.getAttractions(), executorService)
                .thenApply(attractions1 -> attractions1);
//        List<Attraction> attractions = microserviceUserGpsProxy.getAttractions();

        log.info("Service - Calcul en cours...." + user.getUserName());
        //todo retirer log
//        setProximityBuffer(Integer.MAX_VALUE);
        for (VisitedLocation visitedLocation : userLocations) {
            for (Attraction attraction : attractions.join()) {
                if (user.getUserRewards().stream().filter(r -> r.getAttraction().getAttractionName().equals(attraction.getAttractionName())).count() == 0) {
                    if (nearAttraction(visitedLocation, attraction)) {

                        CompletableFuture<UserReward> rewardsPointsFuture = CompletableFuture.supplyAsync(() -> getRewardPoints(attraction.getAttractionId(), user.getUserId()), executorService)
                                .thenApplyAsync(rewardPoints -> new UserReward(visitedLocation, attraction, rewardPoints));
                        rewardsPointsFuture.thenComposeAsync(userReward ->
                                CompletableFuture.runAsync(() -> addUserReward(userReward, user)));

//                        addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction.getAttractionId(), user.getUserId())), user);
                    }
                }
            }
        }
    }

    @Override
    public List<UserReward> getUserRewards(String userName) {
        User user = internalUserMapDAO.getUser(userName);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        return user.getUserRewards();
    }

    public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.getLatitude());
        double lon1 = Math.toRadians(loc1.getLongitude());
        double lat2 = Math.toRadians(loc2.getLatitude());
        double lon2 = Math.toRadians(loc2.getLongitude());

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
    }

    private void addUserReward(UserReward userReward, User user) {
        List<UserReward> userRewards = user.getUserRewards();
        if (userRewards.stream().filter(r -> r.getAttraction().getAttractionName().equals(userReward.getAttraction().getAttractionName())).count() == 0) {
            //TODO quand on change le filter on  enleve la negation(!) le test NearAllAttractions passe au vert
            userRewards.add(userReward);
            log.info("Service - Reward added for use: " + user.getUserName());
            user.setUserRewards(userRewards);
        }
    }

    public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
        return getDistance(attraction, location) > attractionProximityRange ? false : true;
    }

    private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
//        setProximityBuffer(200);
        //todo clean code
        return getDistance(attraction, visitedLocation.getLocation()) > proximityBuffer ? false : true;
    }

    private int getRewardPoints(UUID attractionId, UUID userId) {
        System.out.println("Calculate reward on: " + Thread.currentThread().getName());
        //TODO RETIRER SYS0OUT
        return microserviceRewardsProxy.getRewardsPoints(attractionId, userId);
    }
}


