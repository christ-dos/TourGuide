package com.tripMaster.microservicegps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Class that start the application microservice-gps
 *
 * @author Christine Duarte
 */
@SpringBootApplication
@EnableSwagger2
public class MicroserviceGpsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceGpsApplication.class, args);
    }

}
