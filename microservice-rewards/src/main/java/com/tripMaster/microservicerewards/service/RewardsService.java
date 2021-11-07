package com.tripMaster.microservicerewards.service;

import com.tripMaster.microservicerewards.model.User;

public interface RewardsService {

    void setProximityBuffer(int proximityBuffer);

    void calculateRewards(User user);
}
