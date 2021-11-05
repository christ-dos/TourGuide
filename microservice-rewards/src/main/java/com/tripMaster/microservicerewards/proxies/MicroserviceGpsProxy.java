package com.tripMaster.microservicerewards.proxies;

import com.tripMaster.microservicerewards.model.Attraction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "${microservice-gps.name}", url = "${microservice-gps.url}")
public interface MicroserviceGpsProxy {

    @GetMapping("/getAttractions")
    List<Attraction> getAttractions();
}
