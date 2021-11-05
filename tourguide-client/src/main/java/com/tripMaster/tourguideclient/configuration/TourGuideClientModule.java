package com.tripMaster.tourguideclient.configuration;

import gpsUtil.GpsUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TourGuideClientModule {
	
	@Bean
	public GpsUtil getGpsUtil() {
		return new GpsUtil();
	}
	
}
