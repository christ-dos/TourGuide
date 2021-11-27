package com.tripMaster.tourguideclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Locale;

/**
 * Class that start the application TourGuideClientApplication
 *
 * @author Christine Duarte
 */
@SpringBootApplication
@EnableFeignClients("com.tripMaster.tourguideclient")
@EnableSwagger2
@Slf4j
public class TourGuideClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(TourGuideClientApplication.class, args);

        try {
            Locale.setDefault(new Locale("en", "US"));
        } catch (NumberFormatException ex) {
            log.debug("NumberFormatException: " + ex.getMessage());
        }

    }

}
