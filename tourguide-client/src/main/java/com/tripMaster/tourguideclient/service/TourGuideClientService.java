package com.tripMaster.tourguideclient.service;

import com.tripMaster.tourguideclient.model.Provider;
import com.tripMaster.tourguideclient.model.VisitedLocation;

import java.util.List;

public interface TourGuideClientService {

    VisitedLocation getUserLocation(String userName);

    List<Provider> getTripDeals(String userName);
}
