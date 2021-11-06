package com.tripMaster.tourguideclient.controller;

import com.tripMaster.tourguideclient.model.UserTourGuideClient;
import com.tripMaster.tourguideclient.model.VisitedLocationTourGuideClient;
import com.tripMaster.tourguideclient.proxies.MicroserviceRewardsProxy;
import com.tripMaster.tourguideclient.proxies.MicroserviceUserGpsProxy;
import com.tripMaster.tourguideclient.service.TourGuideClientService;
import com.tripMaster.tourguideclient.service.TourGuideClientServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TourGuideClientController {
    @Autowired
    private MicroserviceUserGpsProxy microserviceUserGpsProxy;

    @Autowired
    private MicroserviceRewardsProxy microserviceRewardsProxy;

    @Autowired
    private TourGuideClientService tourGuideClientService;


    @GetMapping(value = "/getLocation")
    public VisitedLocationTourGuideClient userGpsGetLocation(@RequestParam String userName) {
        VisitedLocationTourGuideClient visitedLocationTourGuideClient = microserviceUserGpsProxy.userGpsGetLocation(userName);
//        UserTourGuideClient user = microserviceUserGpsProxy.getUser(userName);
//        tourGuideClientService.calculateRewards(user);
        log.debug("Controller - request tourGuideClient to get position username: " + userName);
        return visitedLocationTourGuideClient;
    }

    @GetMapping("/getUser")
    public UserTourGuideClient getUser(@RequestParam String userName) {
        UserTourGuideClient user = microserviceUserGpsProxy.getUser(userName);
        log.debug("Controller - request tourguideClient to get user by username: " + userName);

        return user;
    }
}
