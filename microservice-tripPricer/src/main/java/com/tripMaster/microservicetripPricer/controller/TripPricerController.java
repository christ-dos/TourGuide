package com.tripMaster.microservicetripPricer.controller;

import com.tripMaster.microservicetripPricer.service.TripPricerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tripPricer.Provider;

import java.util.List;
import java.util.UUID;

/**
 * Class of controller that manage requests to obtaining providers
 *
 * @author Christine Duarte
 */
@RestController
@Slf4j
public class TripPricerController {
    @Autowired
    private TripPricerService tripPricerService;

    @GetMapping("/getTripDeals")
    public List<Provider> getProviders(String apiKey, @RequestParam UUID attractionId, int adults, int children, int nightsStay, int rewardsPoints) {
        log.info("Controller - request to get list providers in microservice-tripPricer");
        return tripPricerService.getPrice(apiKey, attractionId, adults, children, nightsStay, rewardsPoints);
    }
}
