package com.tripMaster.tourguideclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
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
