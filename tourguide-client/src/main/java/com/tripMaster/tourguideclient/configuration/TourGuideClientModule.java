package com.tripMaster.tourguideclient.configuration;

import com.tripMaster.tourguideclient.DAO.InternalUserMapDAO;
import com.tripMaster.tourguideclient.model.Attraction;
import com.tripMaster.tourguideclient.model.Provider;
import com.tripMaster.tourguideclient.model.VisitedLocation;
import com.tripMaster.tourguideclient.proxies.MicroserviceRewardsProxy;
import com.tripMaster.tourguideclient.proxies.MicroserviceTripPricerProxy;
import com.tripMaster.tourguideclient.proxies.MicroserviceUserGpsProxy;
import com.tripMaster.tourguideclient.service.TourGuideClientRewardsService;
import com.tripMaster.tourguideclient.service.TourGuideClientRewardsServiceImpl;
import com.tripMaster.tourguideclient.utils.Tracker;
import gpsUtil.GpsUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rewardCentral.RewardCentral;

import java.util.List;
import java.util.UUID;

@Configuration
public class TourGuideClientModule {
	//todo clean code suppression class
//	@Bean
//	public InternalUserMapDAO getInternalMapDAP(){
//		return new InternalUserMapDAO();
//	}
//
//	@Bean
//	public TourGuideClientRewardsService getRewardsService() {
//		return new TourGuideClientRewardsServiceImpl(getMicroserviceRewardsProxy(),getMicroserviceUserProxy(),getInternalMapDAP());
//	}


//	@Bean
//	public Tracker getTracker(){
//		return new Tracker();
//	}

//	@Bean
//	public MicroserviceUserGpsProxy getMicroserviceUserProxy(){
//		return new MicroserviceUserGpsProxy() {
//			@Override
//			public VisitedLocation trackUserLocation(UUID userId) {
//				return null;
//			}
//
//			@Override
//			public List<Attraction> getAttractions() {
//				return null;
//			}
//		};
//
//	}
//
//	@Bean
//	public MicroserviceRewardsProxy getMicroserviceRewardsProxy(){
//		return new MicroserviceRewardsProxy() {
//			@Override
//			public int getRewardsPoints(UUID attractionId, UUID userId) {
//				return 0;
//			}
//		};
//	}
//
//	@Bean
//	public MicroserviceTripPricerProxy getMicroserviceTripPricerProxy(){
//		return  new MicroserviceTripPricerProxy() {
//			@Override
//			public List<Provider> getProviders(String apiKey, UUID attractionId, int adults, int children, int nightsStay, int rewardsPoints) {
//				return null;
//			}
//		};
//	}
//
}
