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

    VisitedLocation trackUserLocation(User user); // TODO: Takes userId instead.

    VisitedLocation getUserLocation(User user); // TODO: To remove

    Optional<User> getUserByUserName(String userName); // TODO: To remove

    List<Attraction> getAttractions();
}
