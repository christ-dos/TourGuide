package com.tripMaster.tourguideclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("com.tripMaster.tourguideclient")
public class TourguideClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(TourguideClientApplication.class, args);
	}

}
