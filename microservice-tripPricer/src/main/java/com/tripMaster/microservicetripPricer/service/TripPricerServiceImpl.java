package com.tripMaster.microservicetripPricer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TripPricerServiceImpl implements TripPricerService {

    private TripPricer tripPricer;

    @Autowired
    public TripPricerServiceImpl(TripPricer tripPricer) {

        this.tripPricer = tripPricer;
    }

    public List<Provider> getPrice(String apiKey, UUID attractionId, int adults, int children, int nightsStay, int rewardsPoints) {
        return tripPricer.getPrice(apiKey,attractionId,adults,children,nightsStay,rewardsPoints);
    }
}