package com.tripMaster.microservicegps.controller;

import com.tripMaster.microservicegps.service.UserGpsService;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
    public VisitedLocation trackUserLocation(@RequestParam UUID userId) {
        log.info("Controller - request to get user location with ID: " + userId);
        return userGpsService.trackUserLocation(userId);
    }

    @GetMapping("/getAttractions")
    public List<Attraction> getAttractions() {
        log.info("Request to get list attractions");
        return userGpsService.getAttractions();
    }

}
