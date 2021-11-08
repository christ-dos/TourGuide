package com.tripMaster.microservicetripPricer.controller;

import com.tripMaster.microservicetripPricer.service.TripPricerService;
import com.tripMaster.microservicetripPricer.service.TripPricerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tripPricer.Provider;

import java.util.List;
import java.util.UUID;

@RestController
public class TripPricerController {
    @Autowired
    private TripPricerService tripPricerService;

    @GetMapping("/getTripDeals")
    public List<Provider> getProviders(String apiKey, @RequestParam  UUID attractionId, int adults, int children, int nightsStay, int rewardsPoints) {
        return tripPricerService.getPrice(apiKey, attractionId, adults, children, nightsStay, rewardsPoints);
    }
}
