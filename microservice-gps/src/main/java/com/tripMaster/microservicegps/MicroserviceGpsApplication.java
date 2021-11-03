package com.tripMaster.microservicegps;

import com.tripMaster.microservicegps.model.User;
import com.tripMaster.microservicegps.service.UserGpsService;
import com.tripMaster.microservicegps.service.UserGpsServiceImpl;
import gpsUtil.location.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@SpringBootApplication
public class MicroserviceGpsApplication implements CommandLineRunner {
    @Autowired
    UserGpsService userGpsService;

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceGpsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//
		VisitedLocation visitedLocation = userGpsService.trackUserLocation(user);
        System.out.println(visitedLocation.location.latitude);
		System.out.println(user.getVisitedLocations().get(0).location.latitude);
    }
}
