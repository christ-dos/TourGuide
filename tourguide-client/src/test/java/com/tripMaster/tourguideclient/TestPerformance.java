package com.tripMaster.tourguideclient;


import com.tripMaster.tourguideclient.DAO.InternalUserMapDAO;
import com.tripMaster.tourguideclient.helper.InternalTestHelper;
import com.tripMaster.tourguideclient.model.Attraction;
import com.tripMaster.tourguideclient.model.User;
import com.tripMaster.tourguideclient.model.VisitedLocation;
import com.tripMaster.tourguideclient.proxies.MicroserviceRewardsProxy;
import com.tripMaster.tourguideclient.proxies.MicroserviceTripPricerProxy;
import com.tripMaster.tourguideclient.proxies.MicroserviceUserGpsProxy;
import com.tripMaster.tourguideclient.service.TourGuideClientRewardsServiceImpl;
import com.tripMaster.tourguideclient.service.TourGuideClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang3.time.StopWatch;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPerformance {

	@Autowired
	private MicroserviceUserGpsProxy microserviceUserGpsProxy;
	@Autowired
	private InternalUserMapDAO internalUserMapDAO;
	@Autowired
	private MicroserviceTripPricerProxy microserviceTripPricerProxy;
	@Autowired
	private MicroserviceRewardsProxy microserviceRewardsProxy;

	private TourGuideClientServiceImpl tourGuideClientService;

	private TourGuideClientRewardsServiceImpl tourGuideClientRewardsService;

	@BeforeEach
	public void setUpPerTest() {
		tourGuideClientService = new TourGuideClientServiceImpl(microserviceUserGpsProxy,
				internalUserMapDAO,tourGuideClientRewardsService,microserviceTripPricerProxy);

		tourGuideClientRewardsService = new TourGuideClientRewardsServiceImpl(microserviceRewardsProxy,microserviceUserGpsProxy, internalUserMapDAO);
	}
	
	/*
	 * A note on performance improvements:
	 *     
	 *     The number of users generated for the high volume tests can be easily adjusted via this method:
	 *     
	 *     		InternalTestHelper.setInternalUserNumber(100000);
	 *     
	 *     
	 *     These tests can be modified to suit new solutions, just as long as the performance metrics
	 *     at the end of the tests remains consistent. 
	 * 
	 *     These are performance metrics that we are trying to hit:
	 *     
	 *     highVolumeTrackLocation: 100,000 users within 15 minutes:
	 *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     *
     *     highVolumeGetRewards: 100,000 users within 20 minutes:
	 *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 */
	
//	@Ignore
	@Test
	public void highVolumeTrackLocation() {


//		GpsUtil gpsUtil = new GpsUtil();
//		CompletableFuture<VisitedLocation> completableFuture = new CompletableFuture<>();
//		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		// Users should be incremented up to 100,000, and test finishes within 15 minutes
		InternalTestHelper.setInternalUserNumber(100);


		List<User> allUsers = new ArrayList<>();
		allUsers = tourGuideClientService.getAllUsers();
		
	    StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		//TODO clean code
//		long size = allUsers.stream().parallel().map(u->tourGuideService.trackUserLocation(u)).collect(Collectors.toSet()).size();
//		System.out.println("nombre:" + size);
		for(User user : allUsers) {
			tourGuideClientService.trackUserLocation(user);
//			System.out.println("passage dans la boucle de test:");
		}
		tourGuideClientService.tracker.stopTracking();

		System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}
	
//	@Ignore
	@Test
	public void highVolumeGetRewards() {
//		GpsUtil gpsUtil = new GpsUtil();

		// Users should be incremented up to 100,000, and test finishes within 20 minutes
		InternalTestHelper.setInternalUserNumber(100);
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
//		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

	    Attraction attraction = microserviceUserGpsProxy.getAttractions().get(0);
		List<User> allUsers = tourGuideClientService.getAllUsers();

		allUsers.forEach(user -> tourGuideClientService.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()),user));
		allUsers.forEach(user -> tourGuideClientRewardsService.calculateRewards(user));

		for(User user : allUsers) {
			assertTrue(user.getUserRewards().size() > 0);
		}
		stopWatch.stop();
		tourGuideClientService.tracker.stopTracking();

		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds."); 
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}
	
}
