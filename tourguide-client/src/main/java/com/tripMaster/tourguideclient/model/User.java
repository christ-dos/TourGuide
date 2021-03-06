package com.tripMaster.tourguideclient.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class that models {@link User}
 *
 * @author Christine Duarte
 */
@Getter
@Setter
public class User {
    private final UUID userId;
    private final String userName;
    private String phoneNumber;
    private String emailAddress;
    private Date latestLocationTimestamp;
    private List<UserReward> userRewards = new ArrayList<>();
    private UserPreferences userPreferences = new UserPreferences();
    private List<Provider> tripDeals = new ArrayList<>();
    private CopyOnWriteArrayList<VisitedLocation> visitedLocations = new CopyOnWriteArrayList<>();


    public User(UUID userId, String userName, String phoneNumber, String emailAddress) {
        this.userId = userId;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }


    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", latestLocationTimestamp=" + latestLocationTimestamp +
                ", visitedLocations=" + visitedLocations +
                ", userRewards=" + userRewards +
                ", userPreferences=" + userPreferences +
                ", tripDeals=" + tripDeals +
                '}';
    }
}
