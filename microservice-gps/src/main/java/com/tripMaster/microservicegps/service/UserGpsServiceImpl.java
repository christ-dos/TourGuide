package com.tripMaster.microservicegps.service;

import com.tripMaster.microservicegps.DAO.InternalUserMapDAO;
import com.tripMaster.microservicegps.exception.UserNotFoundException;
import com.tripMaster.microservicegps.model.User;
import com.tripMaster.microservicegps.proxies.MicroserviceRewardsProxy;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

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
    private MicroserviceRewardsProxy microserviceRewardsProxy;

    @Autowired
    public UserGpsServiceImpl(GpsUtil gpsUtil, InternalUserMapDAO internalUserMapDAO, MicroserviceRewardsProxy microserviceRewardsProxy) {
        this.gpsUtil = gpsUtil;
        this.internalUserMapDAO = internalUserMapDAO;
        this.microserviceRewardsProxy = microserviceRewardsProxy;
    }

    @Override
    public VisitedLocation trackUserLocation(User user) {
        Locale.setDefault(new Locale("en", "US"));
        VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
        addToVisitedLocations(visitedLocation, user);
        microserviceRewardsProxy.calculateRewards(user.getUserName());
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
    public Optional<User> getUserByUserName(String userName) {
        User user = internalUserMapDAO.getUser(userName);
        if (user == null) {
            log.error("Service - User not found with username: " + userName);
            throw new UserNotFoundException("user not found");
        }
        log.debug("Service - User found with username: " + userName);
        return Optional.of(user);
    }

    @Override
    public List<Attraction> getAttractions() {
        return gpsUtil.getAttractions();
    }

    private void addToVisitedLocations(VisitedLocation visitedLocation, User user) {
        List<VisitedLocation> visitedLocations = user.getVisitedLocations();
        visitedLocations.add(visitedLocation);
        log.debug("Service - Visited location added for user: " + user.getUserName());
    }
}
