package com.tripMaster.microservicetripPricer.service;

import tripPricer.Provider;

import java.util.List;
import java.util.UUID;

/**
 * Interface that exposes methods of {@link TripPricerServiceImpl}
 *
 * @author Christine Duarte
 */
public interface TripPricerService {
    List<Provider> getPrice(String apiKey, UUID attractionId, int adults, int children, int nightsStay, int rewardsPoints);

}
