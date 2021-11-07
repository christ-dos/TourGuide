package com.tripMaster.tourguideclient.service;

import com.tripMaster.tourguideclient.model.User;
import com.tripMaster.tourguideclient.model.VisitedLocation;

public interface TourGuideClientService {

    VisitedLocation getUserLocation(String userName);
}
