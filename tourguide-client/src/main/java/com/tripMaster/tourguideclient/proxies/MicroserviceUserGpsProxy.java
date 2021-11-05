package com.tripMaster.tourguideclient.proxies;

import com.tripMaster.tourguideclient.model.UserTourGuideClient;
import com.tripMaster.tourguideclient.model.VisitedLocationTourGuideClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${microservice-gps.name}", url = "${microservice-gps.url}")
public interface MicroserviceUserGpsProxy {

    @GetMapping(value = "/getLocation")
    VisitedLocationTourGuideClient userGpsGetLocation(@RequestParam String userName);

    @GetMapping("/getUser")
    UserTourGuideClient getUser(@RequestParam String userName);


}
