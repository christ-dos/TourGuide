package com.tripMaster.microservicerewards.service;

import com.tripMaster.microservicerewards.proxies.MicroserviceGpsProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;

import java.util.UUID;

@Service
@Slf4j
public class RewardsServiceImpl implements RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

    // proximity in miles
    private int defaultProximityBuffer = 10;
    private int proximityBuffer = defaultProximityBuffer;
    private int attractionProximityRange = 200;

    private RewardCentral rewardsCentral;
    private MicroserviceGpsProxy microserviceGpsProxy;

    @Autowired
    public RewardsServiceImpl(RewardCentral rewardsCentral, MicroserviceGpsProxy microserviceGpsProxy) {
        this.rewardsCentral = rewardsCentral;
        this.microserviceGpsProxy = microserviceGpsProxy;
    }

    public int getRewardPoints(UUID attractionId, UUID userId) {
        System.out.println("Calculate reward on: " + Thread.currentThread().getName());
        //TODO RETIRER SYS.OUT
        log.info("rewards points for user:"  + userId);
        return rewardsCentral.getAttractionRewardPoints(attractionId, userId);
    }
}
