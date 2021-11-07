package com.tripMaster.tourguideclient.proxies;

import com.tripMaster.tourguideclient.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${microservice-rewards.name}", url = "${microservice-rewards.url}")
public interface MicroserviceRewardsProxy {

    @GetMapping("/getRewards")
    User calculateRewards(@RequestParam String userName);

}
