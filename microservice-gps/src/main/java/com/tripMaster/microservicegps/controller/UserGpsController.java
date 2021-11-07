package com.tripMaster.microservicegps.controller;

import com.jsoniter.output.JsonStream;
import com.tripMaster.microservicegps.exception.UserNotFoundException;
import com.tripMaster.microservicegps.model.User;
import com.tripMaster.microservicegps.proxies.MicroserviceRewardsProxy;
import com.tripMaster.microservicegps.service.UserGpsService;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Class of controller that manage requests to obtaining user positions
 *
 * @author Christine Duarte
 */
@RestController
@Slf4j
public class UserGpsController {
    @Autowired
    private UserGpsService userGpsService;

    @Autowired
    private MicroserviceRewardsProxy microserviceRewardsProxy;

    @GetMapping("/getLocation")
    public VisitedLocation userGpsGetLocation(@RequestParam String userName) {
        Optional<User> user = userGpsService.getUserByUserName(userName);
        log.debug("Controller - Obtain visited location for user with userName: " + userName);
        return userGpsService.getUserLocation(user.orElseThrow(()-> new UserNotFoundException("User not found")));
    }

    @GetMapping("/trackUser")
    public String trackUser(@RequestParam String userName) {
        Optional<User> user = userGpsService.getUserByUserName(userName);
        microserviceRewardsProxy.calculateRewards(userName);
        VisitedLocation visitedLocation = userGpsService.trackUserLocation(user.get());
        log.info("Controller - request");
        return JsonStream.serialize(getUser(userName).get().getUserRewards());

    }


    @GetMapping("/getUser")
    public Optional<User> getUser(@RequestParam String userName) {
        return userGpsService.getUserByUserName(userName);
    }

    @GetMapping("/getAttractions")
    public List<Attraction> getAttractions() {
        return userGpsService.getAttractions();
    }

    @GetMapping("/getRewards")
    void calculateRewards(@RequestParam String userName){
        microserviceRewardsProxy.calculateRewards(userName);
    }
}
