package com.tripMaster.microservicerewards.service;

import com.tripMaster.microservicerewards.model.User;

public interface RewardsService {

    void calculateRewards(User user);
}
