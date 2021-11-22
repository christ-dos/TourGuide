package com.tripMaster.microservicerewards.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rewardCentral.RewardCentral;

/**
 * Class which configures a bean for lib {@link RewardCentral }
 *
 * @author Christine Duarte
 *
 */
@Configuration
public class MicroServiceRewardsModule {

	@Bean
	public RewardCentral getRewardCentral() {
		return new RewardCentral();
	}
	
}
