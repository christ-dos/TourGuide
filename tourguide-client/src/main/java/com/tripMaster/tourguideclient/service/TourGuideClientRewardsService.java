package com.tripMaster.tourguideclient.service;

import com.tripMaster.tourguideclient.model.User;
import com.tripMaster.tourguideclient.model.UserReward;

import java.util.List;

public interface TourGuideClientRewardsService {

    void calculateRewards(User user);

    List<UserReward> getUserRewards(String userName);
}
