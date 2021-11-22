package com.tripMaster.microservicegps.configuration;

import gpsUtil.GpsUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Class which configures a bean for lib {@link GpsUtil }
 *
 * @author Christine Duarte
 *
 */
@Configuration
public class MicroServiceGpsModule {
	
	@Bean
	public GpsUtil getGpsUtil() {
		return new GpsUtil();
	}
	
}
