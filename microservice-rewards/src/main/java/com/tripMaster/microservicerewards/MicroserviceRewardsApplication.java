package com.tripMaster.microservicerewards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("com.tripMaster.microservicerewards")
public class MicroserviceRewardsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceRewardsApplication.class, args);
	}

}
