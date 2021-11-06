package com.tripMaster.tourguideclient.model;

import gpsUtil.location.VisitedLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserRewardTourGuideClient {

	private final VisitedLocationTourGuideClient visitedLocation;
	private final AttractionTourGuideClient attraction;
	private int rewardPoints;

	public UserRewardTourGuideClient(VisitedLocationTourGuideClient visitedLocation, AttractionTourGuideClient attraction) {
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
