package com.tripMaster.tourguideclient.controller;

import com.tripMaster.tourguideclient.model.UserReward;
import com.tripMaster.tourguideclient.model.VisitedLocation;
import com.tripMaster.tourguideclient.service.TourGuideClientRewardsService;
import com.tripMaster.tourguideclient.service.TourGuideClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class TourGuideClientController {
//    @Autowired
//    private MicroserviceUserGpsProxy microserviceUserGpsProxy;

//    @Autowired
//    private MicroserviceRewardsProxy microserviceRewardsProxy;

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

    @GetMapping("/getRewards")
    public List<UserReward> getRewards(@RequestParam String userName) {
        return tourGuideClientRewardsService.getUserRewards(userName);

    }

}
