package com.tripMaster.microservicetripPricer.service;

import tripPricer.Provider;

import java.util.List;
import java.util.UUID;

public interface TripPricerService {
    List<Provider> getPrice(String apiKey, UUID attractionId, int adults, int children, int nightsStay, int rewardsPoints);

    }
