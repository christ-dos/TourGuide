package com.tripMaster.microservicerewards.model;

import gpsUtil.location.VisitedLocation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserReward {

	private final VisitedLocation visitedLocation;
	private final com.tripMaster.microservicerewards.model.Attraction attraction;
	private int rewardPoints;

	public UserReward(VisitedLocation visitedLocation, com.tripMaster.microservicerewards.model.Attraction attraction, int rewardPoints) {
		this.visitedLocation = visitedLocation;
		this.attraction = attraction;
		this.rewardPoints = rewardPoints;
	}
	
	public UserReward(VisitedLocation visitedLocation, Attraction attraction) {
		this.visitedLocation = visitedLocation;
		this.attraction = attraction;
	}

	public void setRewardPoints(int rewardPoints) {
		this.rewardPoints = rewardPoints;
	}
	
	public int getRewardPoints() {
		return rewardPoints;
	}
	
}
