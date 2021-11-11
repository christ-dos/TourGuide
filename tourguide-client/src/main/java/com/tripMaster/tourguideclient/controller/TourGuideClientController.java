package com.tripMaster.tourguideclient.controller;

import com.tripMaster.tourguideclient.model.*;
import com.tripMaster.tourguideclient.service.TourGuideClientRewardsService;
import com.tripMaster.tourguideclient.service.TourGuideClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    //  TODO: Change this method to no longer return a List of Attractions.
    //  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
    //  Return a new JSON object that contains:
    // Name of Tourist attraction,
    // Tourist attractions lat/long,
    // The user's location lat/long,
    // The distance in miles between the user's location and each of the attractions.
    // The reward points for visiting each Attraction.
    //    Note: Attraction reward points can be gathered from RewardsCentral
    @RequestMapping("/getNearbyAttractions")
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

    @RequestMapping("/getAllCurrentLocations")
    public List<UserCurrentLocation> getAllCurrentLocations() {
        // TODO: Get a list of every user's most recent location as JSON
        //- Note: does not use gpsUtil to query for their current location,
        //        but rather gathers the user's current location from their stored location history.
        //
        // Return object should be the just a JSON mapping of userId to Locations similar to:
        //     {
        //        "019b04a9-067a-4c76-8817-ee75088c3822": {"longitude":-48.188821,"latitude":74.84371}
        //        ...
        //     }
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
