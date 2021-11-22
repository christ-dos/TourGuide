package com.tripMaster.tourguideclient.controller;

import com.tripMaster.tourguideclient.model.*;
import com.tripMaster.tourguideclient.service.TourGuideClientRewardsService;
import com.tripMaster.tourguideclient.service.TourGuideClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Class of controller that manage requests to tour guide client
 *
 * @author Christine Duarte
 */
@RestController
@Slf4j
public class TourGuideClientController {

    @Autowired
    private TourGuideClientService tourGuideClientService;

    @Autowired
    private TourGuideClientRewardsService tourGuideClientRewardsService;

    @GetMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    @GetMapping(value = "/getLocation")
    public VisitedLocation userGpsGetLocation(@RequestParam String userName) {
        VisitedLocation visitedLocation = tourGuideClientService.getUserLocation(userName);
        log.debug("Controller - request tourGuideClient to get position username: " + userName);

        return visitedLocation;
    }

    @GetMapping("/getNearbyAttractions")
    public List<NearByAttraction> getNearbyAttractions(@RequestParam String userName) {
        VisitedLocation visitedLocation = tourGuideClientService.getUserLocation(userName);
        log.debug("Controller - get list of attraction near by position of user: " + userName);

        return tourGuideClientService.getNearByAttractions(visitedLocation);
    }

    @GetMapping("/getRewards")
    public List<UserReward> getRewards(@RequestParam String userName) {
        log.debug("Controller - get list of rewards for username: " + userName);

        return tourGuideClientRewardsService.getUserRewards(userName);
    }

    @GetMapping("/getAllCurrentLocations")
    public List<UserCurrentLocation> getAllCurrentLocations() {
        List<UserCurrentLocation> locations = tourGuideClientService.getAllCurrentLocations();
        log.info("Controller - request to get current position of all users");

        return locations;
    }

    @GetMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
        log.debug("Controller - request to get Trip deals for user: " + userName);

        return tourGuideClientService.getTripDeals(userName);
    }
}
