package com.tripMaster.microservicerewards.service;

import java.util.UUID;

public interface RewardsService {

    int getRewardPoints(UUID attractionId, UUID userId) ;
}
