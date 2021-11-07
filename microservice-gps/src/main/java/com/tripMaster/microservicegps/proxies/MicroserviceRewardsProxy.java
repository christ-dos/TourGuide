package com.tripMaster.microservicegps.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${microservice-rewards.name}", url = "${microservice-rewards.url}")
public interface MicroserviceRewardsProxy {

    @GetMapping("/getRewards")
    void calculateRewards(@RequestParam String userName);

    @GetMapping("/trackUser")
    String trackUser(@RequestParam String userName);

}
