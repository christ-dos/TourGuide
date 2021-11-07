package com.tripMaster.tourguideclient;

import com.tripMaster.tourguideclient.model.User;
import com.tripMaster.tourguideclient.service.TourGuideClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.UUID;

@SpringBootApplication
@EnableFeignClients("com.tripMaster.tourguideclient")
public class TourGuideClientApplication implements CommandLineRunner {
	@Autowired
	TourGuideClientService tourGuideClientService;

	public static void main(String[] args) {
		SpringApplication.run(TourGuideClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

//		List<UserRewardTourGuideClient> rewards = tourGuideClientService.calculateRewards(user);
//		rewards.forEach(r-> System.out.println(r));

	}
}
