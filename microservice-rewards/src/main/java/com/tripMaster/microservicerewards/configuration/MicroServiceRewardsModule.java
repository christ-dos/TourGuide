package com.tripMaster.microservicerewards.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rewardCentral.RewardCentral;

@Configuration
public class MicroServiceRewardsModule {

	@Bean
	public RewardCentral getRewardCentral() {
		return new RewardCentral();
	}
	
}
