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

/**
 * Class of service that manage obtaining user visited location
 *
 * @author Christine Duarte
 */
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
        log.debug("Service - user location tracked for username: " + user.getUserName());
        return visitedLocation;
    }

    @Override
    public VisitedLocation getUserLocation(User user) {
        List<VisitedLocation> visitedLocations = user.getVisitedLocations();

        VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
                user.getVisitedLocations().get(visitedLocations.size() - 1) :
                trackUserLocation(user);
        log.debug("Service - get visited location for user: " + user.getUserName());
        return visitedLocation;
    }

    @Override
    public User getUserByUserName(String userName) {
        User user = internalUserMapDAO.getUser(userName);
        if (user == null) {
            log.error("Service - User not found with username: " + userName);
            throw new UserNotFoundException("user not found");
        }
        log.debug("Service - User found with username: " + userName);
        return user;
    }

    private void addToVisitedLocations(VisitedLocation visitedLocation, User user) {
        List<VisitedLocation> visitedLocations = user.getVisitedLocations();
        visitedLocations.add(visitedLocation);
        log.debug("Service - Visited location added for user: " + user.getUserName());
    }
}
