package com.tripMaster.microservicegps.service;

import com.tripMaster.microservicegps.DAO.InternalUserMapDAO;
import com.tripMaster.microservicegps.model.User;
import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserGpsServiceImplTest {

    private UserGpsServiceImpl userGpsServiceTest;

    private GpsUtil gpsUtil = new GpsUtil();

    @Autowired
    private InternalUserMapDAO internalUserMapDAO;


    @BeforeEach
    public void setUpPerTest() {
        userGpsServiceTest = new UserGpsServiceImpl(gpsUtil,internalUserMapDAO);
    }


    @Test
    public void getUserLocationTest() {
        //GIVEN
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        //WHEN
        VisitedLocation visitedLocationResult = userGpsServiceTest.trackUserLocation(user);
        //THEN
        assertTrue(visitedLocationResult.userId.equals(user.getUserId()));
        assertNotNull(visitedLocationResult.location);
    }

}
