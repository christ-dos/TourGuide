package com.tripMaster.tourguideclient.proxies;

import com.tripMaster.tourguideclient.model.UserRewardTourGuideClient;
import com.tripMaster.tourguideclient.model.UserTourGuideClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "${microservice-rewards.name}", url = "${microservice-rewards.url}")
public interface MicroserviceRewardsProxy {

    @GetMapping("/getRewards")
    void calculateRewards(@RequestBody UserTourGuideClient user);

}
