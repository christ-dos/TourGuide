package com.tripMaster.microservicetripPricer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Class that start the application microservice-tripPricer
 *
 * @author Christine Duarte
 */
@SpringBootApplication
@EnableSwagger2
public class MicroserviceTripPricerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceTripPricerApplication.class, args);
    }

}
