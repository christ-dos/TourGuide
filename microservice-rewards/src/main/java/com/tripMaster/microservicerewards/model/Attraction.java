package com.tripMaster.microservicerewards.model;

import gpsUtil.location.Location;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Attraction extends Location {
    private final String attractionName;
    private final String city;
    private final String state;
    private final UUID attractionId;

    public Attraction(String attractionName, String city, String state, double latitude, double longitude) {
        super(latitude, longitude);
        this.attractionName = attractionName;
        this.city = city;
        this.state = state;
        this.attractionId = UUID.randomUUID();
    }
}

