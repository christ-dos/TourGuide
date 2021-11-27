package com.tripMaster.tourguideclient.service;

import com.tripMaster.tourguideclient.model.NearByAttraction;
import com.tripMaster.tourguideclient.model.Provider;
import com.tripMaster.tourguideclient.model.UserCurrentLocation;
import com.tripMaster.tourguideclient.model.VisitedLocation;

import java.util.List;

/**
 * Interface that exposes methods of {@link TourGuideClientServiceImpl}
 *
 * @author Christine Duarte
 */
public interface TourGuideClientService {

    VisitedLocation getUserLocation(String userName);

    List<UserCurrentLocation> getAllCurrentLocations();

    List<Provider> getTripDeals(String userName);

    List<NearByAttraction> getNearByAttractions(VisitedLocation visitedLocation);
}
