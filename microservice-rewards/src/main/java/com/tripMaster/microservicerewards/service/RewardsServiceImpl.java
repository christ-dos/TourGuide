package com.tripMaster.microservicerewards.service;

import com.tripMaster.microservicerewards.model.*;
import com.tripMaster.microservicerewards.proxies.MicroserviceGpsProxy;
import gpsUtil.location.VisitedLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;

import java.util.List;

@Service
@Slf4j
public class RewardsServiceImpl implements RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

    // proximity in miles
    private int defaultProximityBuffer = 10;
    private int proximityBuffer = defaultProximityBuffer;
    private int attractionProximityRange = 200;

    private RewardCentral rewardsCentral;
    private MicroserviceGpsProxy microserviceGpsProxy;

    @Autowired
    public RewardsServiceImpl(RewardCentral rewardsCentral, MicroserviceGpsProxy microserviceGpsProxy) {
        this.rewardsCentral = rewardsCentral;
        this.microserviceGpsProxy = microserviceGpsProxy;
    }

    public int getDefaultProximityBuffer() {
        return defaultProximityBuffer;
    }

    public void setDefaultProximityBuffer(int defaultProximityBuffer) {
        this.defaultProximityBuffer = defaultProximityBuffer;
    }

    public int getProximityBuffer() {
        return proximityBuffer;
    }

    public void setProximityBuffer(int proximityBuffer) {
        this.proximityBuffer = proximityBuffer;
    }

    public int getAttractionProximityRange() {
        return attractionProximityRange;
    }

    public void setAttractionProximityRange(int attractionProximityRange) {
        this.attractionProximityRange = attractionProximityRange;
    }

    @Override
    public void calculateRewards(User user) {
        List<gpsUtil.location.VisitedLocation> userLocations = user.getVisitedLocations();
        List<Attraction> attractions = microserviceGpsProxy.getAttractions();

        for(gpsUtil.location.VisitedLocation visitedLocation : userLocations) {
            for(Attraction attraction : attractions) {
                if(user.getUserRewards().stream().filter(r -> r.getAttraction().getAttractionName().equals(attraction.getAttractionName())).count() == 0) {
                    if(nearAttraction(visitedLocation, attraction)) {
                        addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)),user);
                    }
                }
            }
        }
    }

    private void addUserReward(UserReward userReward, User user) {
        List<UserReward> userRewards = user.getUserRewards();
        if(userRewards.stream().filter(r -> r.getAttraction().getAttractionName().equals(userReward.getAttraction().getAttractionName())).count() == 0) {
            //TODO quand on change le filter on  enleve la negation(!) le test NearAllAttractions passe au vert
            userRewards.add(userReward);
            user.setUserRewards(userRewards);
        }
    }


    private int getRewardPoints(Attraction attraction, User user) {
        System.out.println("Calculate reward on: " + Thread.currentThread().getName());
        //TODO RETIRER SYS0OUT
        return rewardsCentral.getAttractionRewardPoints(attraction.getAttractionId(), user.getUserId());
    }

    private boolean nearAttraction(gpsUtil.location.VisitedLocation visitedLocation, Attraction attraction) {
        return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
    }

    private double getDistance(gpsUtil.location.Location loc1, gpsUtil.location.Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
    }

}
