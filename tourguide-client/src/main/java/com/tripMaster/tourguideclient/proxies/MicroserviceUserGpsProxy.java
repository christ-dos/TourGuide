package com.tripMaster.tourguideclient.proxies;

import com.tripMaster.tourguideclient.model.Attraction;
import com.tripMaster.tourguideclient.model.Location;
import com.tripMaster.tourguideclient.model.VisitedLocation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

/**
 * An Interface that manage requests send to microservice-UserGps
 *
 * @author Christine Duarte
 */
@FeignClient(name = "${microservice-gps.name}", url = "${microservice-gps.url}")
public interface MicroserviceUserGpsProxy {

    @GetMapping("/getLocation")
    VisitedLocation trackUserLocation(@RequestParam UUID userId);

    @GetMapping("/getAttractions")
    List<Attraction> getAttractions();

    @GetMapping("/getAttractionsbydistance")
    List<Attraction> getAttractionsByAverageDistance(@RequestParam double latitude,
                                                     @RequestParam double longitude);
}
