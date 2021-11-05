package com.tripMaster.microservicerewards.service;

import com.tripMaster.microservicerewards.model.Attraction;
import com.tripMaster.microservicerewards.model.Location;
import com.tripMaster.microservicerewards.model.User;
import com.tripMaster.microservicerewards.model.VisitedLocation;
import com.tripMaster.microservicerewards.proxies.MicroserviceGpsProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

    // proximity in miles
    private int defaultProximityBuffer = 10;
    private int proximityBuffer = defaultProximityBuffer;
    private int attractionProximityRange = 200;

    private MicroserviceGpsProxy microserviceGpsProxy;

    public void calculateRewards(User user) {
        List<VisitedLocation> userLocations = user.getVisitedLocations();
        List<Attraction> attractions = microserviceGpsProxy.getAttractions();

//        for(VisitedLocation visitedLocation : userLocations) {
//            for(Attraction attraction : attractions) {
//                if(user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
//                    if(nearAttraction(visitedLocation, attraction)) {
//                        user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
//                    }
//                }
//            }
//        }
    }

    private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return getDistance(attraction, visitedLocation.getLocation()) > proximityBuffer ? false : true;
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


}
