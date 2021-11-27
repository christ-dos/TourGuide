package com.tripMaster.microservicegps.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;

import java.util.List;
import java.util.UUID;

/**
 * Interface that exposes methods of {@link UserGpsServiceImpl}
 *
 * @author Christine Duarte
 */
public interface UserGpsService {

    VisitedLocation trackUserLocation(UUID userId);

    List<Attraction> getAttractions();

    List<Attraction> getAttractionsByAverageDistance(Location location);
}
