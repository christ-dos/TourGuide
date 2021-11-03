package com.tripMaster.microservicegps.service;

import com.tripMaster.microservicegps.DAO.InternalUserMapDAO;
import com.tripMaster.microservicegps.model.User;
import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@NoArgsConstructor
public class UserGpsServiceImpl implements UserGpsService {
    private final GpsUtil gpsUtil = new GpsUtil();
    private InternalUserMapDAO internalUserMapDAO;

    @Autowired
    public UserGpsServiceImpl(InternalUserMapDAO internalUserMapDAO) {
        this.internalUserMapDAO = internalUserMapDAO;
    }

    @Override
    public VisitedLocation trackUserLocation(User user) {
        Locale.setDefault(new Locale("en", "US"));
        VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
        addToVisitedLocations(visitedLocation, user);
        return visitedLocation;
    }

    @Override
    public VisitedLocation getUserLocation(User user) {
        List<VisitedLocation> visitedLocations = user.getVisitedLocations();

        VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
                user.getVisitedLocations().get(visitedLocations.size() - 1) :
                trackUserLocation(user);
        return visitedLocation;
    }

    @Override
    public User getUser(String userName) {
        return internalUserMapDAO.getUser(userName);
    }

    private void addToVisitedLocations(VisitedLocation visitedLocation, User user) {
        List<VisitedLocation> visitedLocations = user.getVisitedLocations();
        visitedLocations.add(visitedLocation);
    }
}
