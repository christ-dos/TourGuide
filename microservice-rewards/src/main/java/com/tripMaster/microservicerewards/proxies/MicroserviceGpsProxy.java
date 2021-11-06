package com.tripMaster.microservicerewards.proxies;

import com.tripMaster.microservicerewards.model.Attraction;
import com.tripMaster.microservicerewards.model.User;
import com.tripMaster.microservicerewards.model.VisitedLocation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "${microservice-gps.name}", url = "${microservice-gps.url}")
public interface MicroserviceGpsProxy {

    @GetMapping("/getAttractions")
    List<Attraction> getAttractions();

    @GetMapping("/getRewards")
    VisitedLocation getRewards(@RequestParam String userName);

    @GetMapping("/getLocation")
    gpsUtil.location.VisitedLocation userGpsGetLocation(@RequestParam String userName);

    @GetMapping("/getUser")
    User getUser(@RequestParam String userName);
}
