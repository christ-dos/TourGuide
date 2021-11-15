package com.tripMaster.microservicegps;

import com.tripMaster.microservicegps.service.UserGpsService;
import com.tripMaster.microservicegps.service.UserGpsServiceImpl;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class MicroserviceGpsApplication implements CommandLineRunner {
    @Autowired
    UserGpsServiceImpl userGpsService;
    //todo clean code
    public static void main(String[] args) {
        SpringApplication.run(MicroserviceGpsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        VisitedLocation visitedLocation = new VisitedLocation(UUID.randomUUID(), new Location(38.697500D, -9.206667D), new Date());


        List<Attraction> taille = userGpsService.getAttractionsByAverageDistance(visitedLocation.location);
        System.out.println(taille.toString());
    }
}
