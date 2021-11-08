package tourGuide.service;

import java.util.List;

import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.model.User;
import tourGuide.model.UserReward;
@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
    private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;


	public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
	}
	
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}
	
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	public void calculateRewards(User user) {
		List<VisitedLocation> userLocations = user.getVisitedLocations();
		List<Attraction> attractions = gpsUtil.getAttractions();

		for(VisitedLocation visitedLocation : userLocations) {
			for(Attraction attraction : attractions) {
				if(user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
					if(nearAttraction(visitedLocation, attraction)) {
						user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
					}
				}
			}
		}
	}

//	public void calculateRewards(User user) {
//
//		List<VisitedLocation> userLocations = user.getVisitedLocations();
//		List<Attraction> attractions = gpsUtil.getAttractions();
//		//TODO nettoyer code
////		List<VisitedLocation> userLocations = CompletableFuture.supplyAsync(()-> user.getVisitedLocations(),executorService).join();
////		List<Attraction> attractions = CompletableFuture.supplyAsync(()-> gpsUtil.getAttractions(),executorService).join();
//
//		final ExecutorService executorService = Executors.newFixedThreadPool(1000);

//		final ExecutorService executorService = Executors.newFixedThreadPool(Math.min(userLocations.size(),1600), r->{
//			Thread t = new Thread(r);
//			t.setDaemon(true);
//			return t;
//		});

//		for(VisitedLocation visitedLocation : userLocations) {
//			for(Attraction attraction : attractions) {
//				if(user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
//					if(nearAttraction(visitedLocation, attraction)) {
//						CompletableFuture.supplyAsync(() -> getRewardPoints(attraction, user),executorService).thenAccept(rewardPoints -> {
//							UserReward userReward = new UserReward(visitedLocation, attraction, rewardPoints);
//							user.addUserReward(userReward);
////							System.out.println("rewards points: " + rewardPoints);
//						});
//					}
//				}
//			}
//
//		}
//	}

	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}
	
	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}
	
	private int getRewardPoints(Attraction attraction, User user) {
		System.out.println("Calculate reward on: " + Thread.currentThread().getName());
		//TODO RETIRER SYS0OUT
//		CompletableFuture<Integer> completableFutureRewardPoints = CompletableFuture.supplyAsync(
//				()->rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId()));
//		int rewardPoints = completableFutureRewardPoints.join();
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
//		return rewardPoints;
	}

	public void setAttractionProximityRange(int attractionProximityRange) {
		this.attractionProximityRange = attractionProximityRange;
	}

	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
	}

}
