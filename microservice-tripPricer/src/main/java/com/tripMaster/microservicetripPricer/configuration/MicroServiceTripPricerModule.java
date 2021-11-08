package com.tripMaster.microservicetripPricer.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tripPricer.TripPricer;

@Configuration
public class MicroServiceTripPricerModule {
	
	@Bean
	public TripPricer getTripPricer() {
		return new TripPricer();
	}
	

}
