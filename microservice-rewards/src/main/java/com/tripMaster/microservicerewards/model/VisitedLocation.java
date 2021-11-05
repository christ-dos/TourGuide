package com.tripMaster.microservicerewards.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisitedLocation {

    private UUID userId;
    private Location location;
    private Date timeVisited;

}
