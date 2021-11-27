package com.tripMaster.tourguideclient.proxies;

import com.tripMaster.tourguideclient.model.Provider;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

/**
 * An Interface that manage requests send to microservice-TripPricer
 *
 * @author Christine Duarte
 */
@FeignClient(name = "${microservice-tripPricer.name}", url = "${microservice-tripPricer.url}")
public interface MicroserviceTripPricerProxy {

    @GetMapping("/getTripDeals")
    List<Provider> getProviders(@RequestParam String apiKey,
                                @RequestParam UUID attractionId, @RequestParam int adults,
                                @RequestParam int children,
                                @RequestParam int nightsStay, @RequestParam int rewardsPoints);
}
