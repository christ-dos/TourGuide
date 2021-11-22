package com.tripMaster.microservicegps.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Class of service that permit obtains user visited location
 *
 * @author Christine Duarte
 */
@Service
@NoArgsConstructor
@Slf4j
public class UserGpsServiceImpl implements UserGpsService {

    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
    private GpsUtil gpsUtil;


    @Autowired
    public UserGpsServiceImpl(GpsUtil gpsUtil) {
        this.gpsUtil = gpsUtil;
    }

    @Override
    public VisitedLocation trackUserLocation(UUID userId) {
        Locale.setDefault(new Locale("en", "US"));
        log.debug("Service - user location tracked for userId: " + userId);
        return gpsUtil.getUserLocation(userId);
    }

    @Override
    public List<Attraction> getAttractions() {
        log.info("Service - Attraction getted");
        return gpsUtil.getAttractions();
    }

    @Override
    public List<Attraction> getAttractionsByAverageDistance(Location location) {
        List<Attraction> attractions = gpsUtil.getAttractions();
        double average = attractions.stream().mapToDouble(attraction -> getDistance(attraction, location))
                .average()
                .orElse(0D);
        List<Attraction> attractionsNearPosition = attractions.stream().filter(attraction -> getDistance(attraction, location) < average)
                .collect(Collectors.toList());

        return attractionsNearPosition;
    }

    private double getDistance(Location loc1, Location loc2) {
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
