package com.tripMaster.tourguideclient.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Class that models {@link UserReward}
 *
 * @author Christine Duarte
 */
@Getter
@Setter
@AllArgsConstructor
public class UserReward {

    private final VisitedLocation visitedLocation;
    private final Attraction attraction;
    private int rewardPoints;

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

    @Override
    public String toString() {
        return "UserReward{" +
                "visitedLocation=" + visitedLocation +
                ", attraction=" + attraction +
                ", rewardPoints=" + rewardPoints +
                '}';
    }
}
