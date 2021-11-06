package com.tripMaster.tourguideclient.service;

import com.tripMaster.tourguideclient.model.UserRewardTourGuideClient;
import com.tripMaster.tourguideclient.model.UserTourGuideClient;

import java.util.List;

public interface TourGuideClientService {
    List<UserRewardTourGuideClient> calculateRewards(UserTourGuideClient user);
}
