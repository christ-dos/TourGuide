package com.tripMaster.microservicerewards.service;

import java.util.UUID;

/**
 * Interface that exposes methods of {@link RewardsServiceImpl}
 *
 * @author  Christine Duarte
 */
public interface RewardsService {

    int getRewardPoints(UUID attractionId, UUID userId) ;
}
