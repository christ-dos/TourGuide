package com.tripMaster.tourguideclient.service;

import com.tripMaster.tourguideclient.model.User;
import com.tripMaster.tourguideclient.model.UserReward;

import java.util.List;

/**
 * Interface that exposes methods of {@link TourGuideClientRewardsServiceImpl}
 *
 * @author Christine Duarte
 */
public interface TourGuideClientRewardsService {

    void calculateRewards(User user);

    List<UserReward> getUserRewards(String userName);
}
