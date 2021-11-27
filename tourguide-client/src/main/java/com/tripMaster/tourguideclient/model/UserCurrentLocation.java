package com.tripMaster.tourguideclient.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Class that models {@link UserCurrentLocation}
 * and display current position of users
 *
 * @author Christine Duarte
 */
@Getter
@Setter
@AllArgsConstructor
public class UserCurrentLocation {

    UUID userId;
    Location location;

    @Override
    public String toString() {
        return "UserCurrentLocation{" +
                "userId=" + userId +
                ", location=" + location +
                '}';
    }
}
