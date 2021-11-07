package com.tripMaster.tourguideclient.service;

import com.tripMaster.tourguideclient.DAO.InternalUserMapDAO;
import com.tripMaster.tourguideclient.exception.UserNotFoundException;
import com.tripMaster.tourguideclient.model.User;
import com.tripMaster.tourguideclient.model.VisitedLocation;
import com.tripMaster.tourguideclient.proxies.MicroserviceUserGpsProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class TourGuideClientServiceImpl implements TourGuideClientService {

//    private MicroserviceRewardsProxy microserviceRewardsProxy;

    private MicroserviceUserGpsProxy microserviceUserGpsProxy;

    private InternalUserMapDAO internalUserMapDAO;

    private TourGuideClientRewardsServiceImpl tourGuideClientRewardsServiceImpl;

    @Autowired
    public TourGuideClientServiceImpl(MicroserviceUserGpsProxy microserviceUserGpsProxy, InternalUserMapDAO internalUserMapDAO, TourGuideClientRewardsServiceImpl tourGuideClientRewardsServiceImpl) {
        this.microserviceUserGpsProxy = microserviceUserGpsProxy;
        this.internalUserMapDAO = internalUserMapDAO;
        this.tourGuideClientRewardsServiceImpl = tourGuideClientRewardsServiceImpl;
    }

    public VisitedLocation trackUserLocation(User user) {
        Locale.setDefault(new Locale("en", "US"));
        VisitedLocation visitedLocation = microserviceUserGpsProxy.trackUserLocation(user.getUserId());
        addToVisitedLocations(visitedLocation, user);
        tourGuideClientRewardsServiceImpl.calculateRewards(user);
        log.debug("Service - user location tracked for username: " + user.getUserName());
        return visitedLocation;
    }

    @Override
    public VisitedLocation getUserLocation(String userName) {
        User user = internalUserMapDAO.getUser(userName);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        List<VisitedLocation> visitedLocations = user.getVisitedLocations();

        VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
                user.getVisitedLocations().get(visitedLocations.size() - 1) :
                trackUserLocation(user);

        log.debug("Service - get user location for user: " + user.getUserName());
        return visitedLocation;
    }

    private void addToVisitedLocations(VisitedLocation visitedLocation, User user) {
        List<VisitedLocation> visitedLocations = user.getVisitedLocations();
        visitedLocations.add(visitedLocation);
        log.debug("Service - Visited location added for user: " + user.getUserName());
    }
}
