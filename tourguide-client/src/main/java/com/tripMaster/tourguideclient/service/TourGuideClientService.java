package com.tripMaster.tourguideclient.service;

import com.tripMaster.tourguideclient.model.UserReward;
import com.tripMaster.tourguideclient.model.User;

import java.util.List;

public interface TourGuideClientService {

    List<UserReward> calculateRewards(User user);
}
