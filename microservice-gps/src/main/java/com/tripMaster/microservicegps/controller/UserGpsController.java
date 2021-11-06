package com.tripMaster.microservicegps.controller;

import com.tripMaster.microservicegps.exception.UserNotFoundException;
import com.tripMaster.microservicegps.model.User;
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

    @GetMapping("/getLocation")
    public VisitedLocation userGpsGetLocation(@RequestParam String userName) {
        Optional<User> user = userGpsService.getUserByUserName(userName);
        log.debug("Controller - Obtain visited location for user with userName: " + userName);
        return userGpsService.getUserLocation(user.orElseThrow(()-> new UserNotFoundException("User not found")));
    }

    @GetMapping("/getUser")
    public Optional<User> getUser(@RequestParam String userName) {
        return userGpsService.getUserByUserName(userName);
    }

    @GetMapping("/getAttractions")
    public List<Attraction> getAttractions() {
        return userGpsService.getAttractions();
    }
}
