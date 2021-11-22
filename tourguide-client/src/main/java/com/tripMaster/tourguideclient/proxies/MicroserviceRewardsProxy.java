package com.tripMaster.tourguideclient.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

/**
 * An Interface that manage requests send to microservice-Rewards
 *
 * @author Christine Duarte
 */
@FeignClient(name = "${microservice-rewards.name}", url = "${microservice-rewards.url}")
public interface MicroserviceRewardsProxy {

    @GetMapping("/getRewards")
    int getRewardsPoints(@RequestParam UUID attractionId, @RequestParam UUID userId);

}
