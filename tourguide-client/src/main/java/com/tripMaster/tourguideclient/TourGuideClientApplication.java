package com.tripMaster.tourguideclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableFeignClients("com.tripMaster.tourguideclient")
@EnableSwagger2
public class TourGuideClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(TourGuideClientApplication.class, args);
    }

}
