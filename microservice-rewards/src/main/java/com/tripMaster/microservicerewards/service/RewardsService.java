package com.tripMaster.microservicerewards.service;

import com.tripMaster.microservicerewards.model.Attraction;
import com.tripMaster.microservicerewards.model.User;

import java.util.UUID;

public interface RewardsService {

    int getRewardPoints(UUID attractionId, UUID userId) ;
}
