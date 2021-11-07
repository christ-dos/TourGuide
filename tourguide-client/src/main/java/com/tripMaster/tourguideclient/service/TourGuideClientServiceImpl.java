package com.tripMaster.tourguideclient.service;

import com.tripMaster.tourguideclient.model.UserReward;
import com.tripMaster.tourguideclient.model.User;
import com.tripMaster.tourguideclient.proxies.MicroserviceRewardsProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TourGuideClientServiceImpl implements TourGuideClientService {
    @Autowired
    private MicroserviceRewardsProxy microserviceRewardsProxy;

    @Override
    public List<UserReward> calculateRewards(User user) {
        List<UserReward> rewards = user.getUserRewards();
//        rewards.forEach(r -> System.out.println(r));
//        log.info("Service -  calculate Rewards:" + rewards);
        return user.getUserRewards();
    }
}
