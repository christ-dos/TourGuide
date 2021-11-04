package com.tripMaster.microservicegps.service;

import com.tripMaster.microservicegps.model.User;
import gpsUtil.location.VisitedLocation;

/**
 * Interface that exposes methodes of {@link UserGpsServiceImpl}
 *
 * @author  Christine Duarte
 */
public interface UserGpsService {

    VisitedLocation trackUserLocation(User user);

    VisitedLocation getUserLocation(User user);

    User getUserByUserName(String userName);

}
