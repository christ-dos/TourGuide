package com.tripMaster.microservicegps;

import com.tripMaster.microservicegps.service.UserGpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.UUID;

@SpringBootApplication
@EnableFeignClients("com.tripMaster.microservicegps")
public class MicroserviceGpsApplication {
    @Autowired
    UserGpsService userGpsService;

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceGpsApplication.class, args);
    }
}
