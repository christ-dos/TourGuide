package com.tripMaster.tourguideclient.proxies;

import com.tripMaster.tourguideclient.model.Attraction;
import com.tripMaster.tourguideclient.model.VisitedLocation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "${microservice-gps.name}", url = "${microservice-gps.url}")
public interface MicroserviceUserGpsProxy {

    @GetMapping("/getLocation")
    VisitedLocation trackUserLocation(UUID userId);

    @GetMapping("/getAttractions")
    List<Attraction> getAttractions();
}
