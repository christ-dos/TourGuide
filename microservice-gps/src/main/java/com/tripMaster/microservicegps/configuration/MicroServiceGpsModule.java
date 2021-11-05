package com.tripMaster.microservicegps.configuration;

import gpsUtil.GpsUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MicroServiceGpsModule {
	
	@Bean
	public GpsUtil getGpsUtil() {
		return new GpsUtil();
	}
	
}
