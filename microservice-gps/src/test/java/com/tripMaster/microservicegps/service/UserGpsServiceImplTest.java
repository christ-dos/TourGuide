package com.tripMaster.microservicegps.service;

import com.tripMaster.microservicegps.DAO.InternalUserMapDAO;
import com.tripMaster.microservicegps.exception.UserNotFoundException;
import com.tripMaster.microservicegps.model.User;
import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
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

    @Mock
    private InternalUserMapDAO internalUserMapDAOMock;

    private User userTest;


    @BeforeEach
    public void setUpPerTest() {
        userGpsServiceTest = new UserGpsServiceImpl(gpsUtilMock, internalUserMapDAOMock);
        userTest = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

    }

    @Test
    public void trackUserLocationTest(){
        VisitedLocation visitedLocationMock = new VisitedLocation(userTest.getUserId(), new Location(33.817595D, -116.922008D), new Date());

        when(gpsUtilMock.getUserLocation(userTest.getUserId())).thenReturn(visitedLocationMock);
        //WHEN
        assertTrue(userTest.getVisitedLocations().isEmpty());
        VisitedLocation visitedLocationResult = userGpsServiceTest.trackUserLocation(userTest);
        //THEN
        assertTrue(!userTest.getVisitedLocations().isEmpty());
        assertEquals(-116.922008D, visitedLocationResult.location.longitude);
        assertEquals(33.817595D, visitedLocationResult.location.latitude);

        verify(gpsUtilMock, times(1)).getUserLocation(any(UUID.class));
    }

    @Test
    public void getUserLocationTest_whenVisitedLocationsListIsEmpty_thenCallMethodTrackUserLoaction() {
        //GIVEN
        VisitedLocation visitedLocationMock = new VisitedLocation(userTest.getUserId(), new Location(33.817595D, -116.922008D), new Date());
        List<VisitedLocation> emptyListTest = new ArrayList<>();
        userTest.setVisitedLocations(emptyListTest);

        when(gpsUtilMock.getUserLocation(userTest.getUserId())).thenReturn(visitedLocationMock);
        //WHEN
        assertTrue(userTest.getVisitedLocations().isEmpty());
        VisitedLocation visitedLocationResult = userGpsServiceTest.getUserLocation(userTest);
        //THEN
        assertTrue(visitedLocationResult.userId.equals(userTest.getUserId()));
        assertTrue(userTest.getVisitedLocations().size() > 0);
        assertEquals(-116.922008D,visitedLocationResult.location.longitude);
        assertEquals(33.817595D,visitedLocationResult.location.latitude);

        verify(gpsUtilMock, times(1)).getUserLocation(any(UUID.class));
    }

    @Test
    public void getUserLocationTest_whenVisitedLocationsListIsNotEmpty_thenGetLastVisitedLocation() {
        //GIVEN
        User userTest2 = new User(UUID.randomUUID(), "jona", "000", "jona@tourGuide.com");

        List<VisitedLocation> visitedLocationListTest = Arrays.asList(
                new VisitedLocation(userTest2.getUserId(), new Location(33.817595D, -116.922008D), new Date()),
                new VisitedLocation(userTest2.getUserId(), new Location(34.817595D, -117.922008D), new Date()),
                new VisitedLocation(userTest2.getUserId(), new Location(35.817595D, -118.922008D), new Date())
        );
        userTest2.setVisitedLocations(visitedLocationListTest);
        //WHEN
        VisitedLocation visitedLocationResult = userGpsServiceTest.getUserLocation(userTest2);
        //THEN
        assertEquals(userTest2.getUserId(),visitedLocationResult.userId);
        assertEquals(visitedLocationListTest.get(2),visitedLocationResult);
        assertTrue(userTest2.getVisitedLocations().size() > 0);
        assertEquals(-118.922008D, visitedLocationResult.location.longitude);
        assertEquals(35.817595D, visitedLocationResult.location.latitude);
    }

    @Test
    public void getUserByUserNameTest_whenUserExistAndIsJon_thenReturnUserJon() {
        //GIVEN
        when(internalUserMapDAOMock.getUser(anyString())).thenReturn(userTest);
        //WHEN
        User userResult = userGpsServiceTest.getUserByUserName("jon");
        //THEN
        assertTrue(userResult.getUserId() == userTest.getUserId());
        assertTrue(userResult.getEmailAddress().contains("jon"));
        verify(internalUserMapDAOMock,times(1)).getUser(anyString());
    }

    @Test
    public void getUserByUserNameTest_whenUserNotExistAndIsUnknown_thenReturnNull() {
        //GIVEN
        when(internalUserMapDAOMock.getUser(anyString())).thenReturn(null);
        //WHEN
        //THEN
        assertThrows(UserNotFoundException.class, () -> userGpsServiceTest.getUserByUserName("Unknown"));
        verify(internalUserMapDAOMock,times(1)).getUser(anyString());
    }



}
