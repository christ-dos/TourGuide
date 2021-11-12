package com.tripMaster.tourguideclient.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NearByAttraction implements Comparable {
    String attractionName;
    Location attractionLocation;
    Location userLocation;
    int distance;
    int rewardsPoints;

    @Override
    public String toString() {
        return "NearByAttraction{" +
                "attractionName='" + attractionName + '\'' +
                ", attractionLocation=" + attractionLocation +
                ", userLocation=" + userLocation +
                ", distance=" + distance +
                ", rewardsPoints=" + rewardsPoints +
                '}';
    }

    @Override
    public int compareTo(Object compareTo) {
        NearByAttraction compareToNearby = (NearByAttraction) compareTo;
        if (distance == ((NearByAttraction) compareTo).distance) {
            return 0;
        }
        if (distance > ((NearByAttraction) compareTo).distance) {
            return 1;
        }
        return -1;

    }
}
