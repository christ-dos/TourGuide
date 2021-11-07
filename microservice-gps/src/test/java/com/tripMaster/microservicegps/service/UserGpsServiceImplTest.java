package com.tripMaster.microservicegps.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Class that test the {@link UserGpsServiceImpl}
 *
 * @author Christine Duarte
 *
 */
@ExtendWith(MockitoExtension.class)
public class UserGpsServiceImplTest {

    private UserGpsServiceImpl userGpsServiceTest;

    @Mock
    private GpsUtil gpsUtilMock;

    @BeforeEach
    public void setUpPerTest() {
        userGpsServiceTest = new UserGpsServiceImpl(gpsUtilMock);
    }

    @Test
    public void trackUserLocationTest(){
        //GIVEN
        UUID userId = UUID.randomUUID();
        VisitedLocation visitedLocationMock = new VisitedLocation(userId, new Location(33.817595D, -116.922008D), new Date());
        when(gpsUtilMock.getUserLocation(any(UUID.class))).thenReturn(visitedLocationMock);
        //WHEN
        VisitedLocation visitedLocationResult = userGpsServiceTest.trackUserLocation(userId);
        //THEN
        assertTrue(visitedLocationResult.userId == userId);
        assertEquals(-116.922008D, visitedLocationResult.location.longitude);
        assertEquals(33.817595D, visitedLocationResult.location.latitude);

        verify(gpsUtilMock, times(1)).getUserLocation(any(UUID.class));
    }

    @Test
    public void getAttractionsTest(){
        //GIVEN
        List<Attraction> attractions = new ArrayList();
        attractions.add(new Attraction("Disneyland", "Anaheim", "CA", 33.817595D, -117.922008D));
        attractions.add(new Attraction("Jackson Hole", "Jackson Hole", "WY", 43.582767D, -110.821999D));
        attractions.add(new Attraction("Mojave National Preserve", "Kelso", "CA", 35.141689D, -115.510399D));
        when(gpsUtilMock.getAttractions()).thenReturn(attractions);
        //WHEN
        List<Attraction> attractionsResult = gpsUtilMock.getAttractions();
        //THEN
        assertTrue(attractionsResult.size() == 3);
        assertEquals("Disneyland",attractionsResult.get(0).attractionName);
        assertEquals(33.817595D,attractionsResult.get(0).latitude);
        assertEquals(-117.922008D,attractionsResult.get(0).longitude);
    }

}
