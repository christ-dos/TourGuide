package com.tripMaster.microservicerewards.controller;

import com.jsoniter.output.JsonStream;
import com.tripMaster.microservicerewards.model.Attraction;
import com.tripMaster.microservicerewards.model.User;
import com.tripMaster.microservicerewards.model.VisitedLocation;
import com.tripMaster.microservicerewards.proxies.MicroserviceGpsProxy;
import com.tripMaster.microservicerewards.service.RewardsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
public class RewardsController {
    @Autowired
    private MicroserviceGpsProxy microserviceGpsProxy;

    @Autowired
    private RewardsService rewardsService;

    @GetMapping("/getRewards")
    public int getRewardsPoints(@RequestParam UUID attractionId, @RequestParam UUID userId) {
        log.info("Controller - request to get rewards points");
        return rewardsService.getRewardPoints(attractionId,userId);

    }
}
