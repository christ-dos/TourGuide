package com.tripMaster.microservicerewards.controller;

import com.tripMaster.microservicerewards.model.Attraction;
import com.tripMaster.microservicerewards.model.User;
import com.tripMaster.microservicerewards.model.VisitedLocation;
import com.tripMaster.microservicerewards.proxies.MicroserviceGpsProxy;
import com.tripMaster.microservicerewards.service.RewardsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RewardsController {
    @Autowired
    private MicroserviceGpsProxy microserviceGpsProxy;

    @Autowired
    private RewardsService rewardsService;


    @GetMapping("/getAttractions")
    public List<Attraction> getAttractions() {
        return microserviceGpsProxy.getAttractions();
    }

    @GetMapping("/getRewards")
    public void calculateRewards(@RequestParam String userName) {
        User user = microserviceGpsProxy.getUser(userName);
        rewardsService.calculateRewards(user);
    }

    @GetMapping("/getLocation")
    public VisitedLocation userGpsGetLocation(@RequestParam String userName) {
        return microserviceGpsProxy.userGpsGetLocation(userName);
    }

    @GetMapping("/getUser")
    public User getUser(@RequestParam String userName) {
        return microserviceGpsProxy.getUser(userName);
    }

}
