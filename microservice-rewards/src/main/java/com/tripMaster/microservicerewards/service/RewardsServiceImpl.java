package com.tripMaster.microservicerewards.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;

import java.util.UUID;

/**
 * Class of service that permit get rewards points
 *
 * @author Christine Duarte
 */
@Service
@Slf4j
public class RewardsServiceImpl implements RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

    private RewardCentral rewardsCentral;

    @Autowired
    public RewardsServiceImpl(RewardCentral rewardsCentral) {
        this.rewardsCentral = rewardsCentral;
    }

    public int getRewardPoints(UUID attractionId, UUID userId) {
        log.info("Service - Rewards points getted for user:"  + userId);
        return rewardsCentral.getAttractionRewardPoints(attractionId, userId);
    }
}
