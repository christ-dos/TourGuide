package com.tripMaster.microservicerewards.controller;

import com.tripMaster.microservicerewards.model.Attraction;
import com.tripMaster.microservicerewards.proxies.MicroserviceGpsProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
}
