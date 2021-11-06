package com.tripMaster.microservicegps.service;

import com.tripMaster.microservicegps.model.User;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

import java.util.List;
import java.util.Optional;

/**
 * Interface that exposes methodes of {@link UserGpsServiceImpl}
 *
 * @author  Christine Duarte
 */
public interface UserGpsService {

    VisitedLocation trackUserLocation(User user);

    VisitedLocation getUserLocation(User user);

    Optional<User> getUserByUserName(String userName);

    List<Attraction> getAttractions();
}
