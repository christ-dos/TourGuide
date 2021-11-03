package com.tripMaster.microservicegps.controller;

import com.tripMaster.microservicegps.service.UserGpsService;
import gpsUtil.location.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserGpsController {
    @Autowired
    private UserGpsService userGpsService;

    @GetMapping("/getLocation")
    public VisitedLocation getLocation(@RequestParam String userName) {
        return userGpsService.getUserLocation(userGpsService.getUser(userName));
    }
}
