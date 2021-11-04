package com.tripMaster.microservicegps.service;

import com.tripMaster.microservicegps.DAO.InternalUserMapDAO;
import com.tripMaster.microservicegps.exception.UserNotFoundException;
import com.tripMaster.microservicegps.model.User;
import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@NoArgsConstructor
@Slf4j
public class UserGpsServiceImpl implements UserGpsService {
    private GpsUtil gpsUtil;
    private InternalUserMapDAO internalUserMapDAO;

    @Autowired
    public UserGpsServiceImpl(GpsUtil gpsUtil, InternalUserMapDAO internalUserMapDAO) {
        this.gpsUtil = gpsUtil;
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
    public User getUserByUserName(String userName) {
        User user = internalUserMapDAO.getUser(userName);
        if(user == null){
            throw new UserNotFoundException("user not found");
        }
        return user;
    }

    private void addToVisitedLocations(VisitedLocation visitedLocation, User user) {
        List<VisitedLocation> visitedLocations = user.getVisitedLocations();
        visitedLocations.add(visitedLocation);
    }
}
