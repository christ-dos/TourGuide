package com.tripMaster.microservicerewards.controller;

import com.jsoniter.output.JsonStream;
import com.tripMaster.microservicerewards.model.Attraction;
import com.tripMaster.microservicerewards.model.User;
import com.tripMaster.microservicerewards.model.VisitedLocation;
import com.tripMaster.microservicerewards.proxies.MicroserviceGpsProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RewardsController {
    @Autowired
    private MicroserviceGpsProxy microserviceGpsProxy;


    @GetMapping("/getAttractions")
    public List<Attraction> getAttractions(){
        return microserviceGpsProxy.getAttractions();
    }

    @GetMapping("/getRewards")
    public VisitedLocation getRewards(@RequestParam String userName) {
        return null;
//        return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }

    @GetMapping("/getLocation")
    gpsUtil.location.VisitedLocation userGpsGetLocation(@RequestParam String userName){
        return microserviceGpsProxy.userGpsGetLocation(userName);
    }

//    @GetMapping("/getUser")
//    public User getUser(@RequestParam String userName) {
//        return microserviceGpsProxy.getUser(userName);
//    }

}
