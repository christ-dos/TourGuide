package com.tripMaster.microservicegps.service;

import com.tripMaster.microservicegps.model.User;
import gpsUtil.location.VisitedLocation;

public interface UserGpsService {

    VisitedLocation trackUserLocation(User user);

    VisitedLocation getUserLocation(User user);

    User getUserByUserName(String userName);

}
