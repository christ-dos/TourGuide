package com.tripMaster.tourguideclient.controller;

import com.tripMaster.tourguideclient.model.UserTourGuideClient;
import com.tripMaster.tourguideclient.model.VisitedLocation;
import com.tripMaster.tourguideclient.proxies.MicroserviceUserGpsProxy;
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


    @GetMapping(value = "/getLocation")
    public VisitedLocation userGpsGetLocation(@RequestParam String userName) {
        VisitedLocation visitedLocation = microserviceUserGpsProxy.userGpsGetLocation(userName);
        log.debug("Controller - request tourguideClient to get position username: " + userName);
        return visitedLocation;
    }

    @GetMapping("/getUser")
    public UserTourGuideClient getUser(@RequestParam String userName) {
        UserTourGuideClient user = microserviceUserGpsProxy.getUser(userName);
        log.debug("Controller - request tourguideClient to get user by username: " + userName);

        return user;
    }
}
