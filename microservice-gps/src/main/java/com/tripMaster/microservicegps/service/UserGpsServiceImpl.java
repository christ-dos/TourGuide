package com.tripMaster.microservicegps.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Class of service that manage obtaining user visited location
 *
 * @author Christine Duarte
 */
@Service
@NoArgsConstructor
@Slf4j
public class UserGpsServiceImpl implements UserGpsService {
    private GpsUtil gpsUtil;

    @Autowired
    public UserGpsServiceImpl(GpsUtil gpsUtil) {
        this.gpsUtil = gpsUtil;
    }

    @Override
    public VisitedLocation trackUserLocation(UUID userId) {
        Locale.setDefault(new Locale("en", "US"));
        log.debug("Service - user location tracked for userId: " + userId);
        return gpsUtil.getUserLocation(userId);
    }

    @Override
    public List<Attraction> getAttractions() {
        log.info("Service - Attraction getted");
        return gpsUtil.getAttractions();
    }

}
