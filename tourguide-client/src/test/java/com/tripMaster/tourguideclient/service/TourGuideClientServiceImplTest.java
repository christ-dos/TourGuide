package com.tripMaster.tourguideclient.service;

import com.tripMaster.tourguideclient.DAO.InternalUserMapDAO;
import com.tripMaster.tourguideclient.exception.UserNotFoundException;
import com.tripMaster.tourguideclient.model.Location;
import com.tripMaster.tourguideclient.model.User;
import com.tripMaster.tourguideclient.model.VisitedLocation;
import com.tripMaster.tourguideclient.proxies.MicroserviceRewardsProxy;
import com.tripMaster.tourguideclient.proxies.MicroserviceUserGpsProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TourGuideClientServiceImplTest {

    private TourGuideClientServiceImpl tourGuideClientServiceTest;

    @Mock
    private MicroserviceRewardsProxy microserviceRewardsProxy;

    @Mock
    private MicroserviceUserGpsProxy microserviceUserGpsProxy;

    @Mock
    private InternalUserMapDAO internalUserMapDAO;

    private User userTest;

    @BeforeEach
    public void setUpPerTest() {
        tourGuideClientServiceTest = new TourGuideClientServiceImpl(
                microserviceRewardsProxy,microserviceUserGpsProxy,internalUserMapDAO);
        userTest = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    }

    @Test
    public void trackUserLocationTest(){
        //GIVEN
        VisitedLocation visitedLocationMock = new com.tripMaster.tourguideclient.model.VisitedLocation(userTest.getUserId(), new Location(33.817595D, -116.922008D), new Date());
        when(microserviceUserGpsProxy.trackUserLocation(any(UUID.class))).thenReturn(visitedLocationMock);
        //WHEN
        VisitedLocation visitedLocationResult = tourGuideClientServiceTest.trackUserLocation(userTest);
        //THEN
        assertTrue(visitedLocationResult.getUserId() == userTest.getUserId());
        assertEquals(-116.922008D, visitedLocationResult.getLocation().getLongitude());
        assertEquals(33.817595D, visitedLocationResult.getLocation().getLatitude());

        verify(microserviceUserGpsProxy, times(1)).trackUserLocation(any(UUID.class));
    }

    @Test
    public void getUserLocationTest_whenVisitedLocationsListIsEmpty_thenCallMethodTrackUserLocation() {
        //GIVEN
        VisitedLocation visitedLocationMock = new VisitedLocation(userTest.getUserId(), new Location(33.817595D, -116.922008D), new Date());
        List<VisitedLocation> emptyListTest = new ArrayList<>();
        userTest.setVisitedLocations(emptyListTest);
        when(internalUserMapDAO.getUser(anyString())).thenReturn(userTest);
        when(microserviceUserGpsProxy.trackUserLocation(userTest.getUserId())).thenReturn(visitedLocationMock);
        //WHEN
        assertTrue(userTest.getVisitedLocations().isEmpty());
        VisitedLocation visitedLocationResult = tourGuideClientServiceTest.getUserLocation(userTest.getUserName());
        //THEN
        assertTrue(visitedLocationResult.getUserId().equals(userTest.getUserId()));
        assertTrue(userTest.getVisitedLocations().size() > 0);
        assertEquals(-116.922008D,visitedLocationResult.getLocation().getLongitude());
        assertEquals(33.817595D,visitedLocationResult.getLocation().getLatitude());

        verify(microserviceUserGpsProxy, times(1)).trackUserLocation(any(UUID.class));
        verify(internalUserMapDAO,times(1)).getUser(anyString());
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
        when(internalUserMapDAO.getUser(anyString())).thenReturn(userTest2);
        //WHEN
        VisitedLocation visitedLocationResult = tourGuideClientServiceTest.getUserLocation(userTest2.getUserName());
        //THEN
        assertEquals(userTest2.getUserId(),visitedLocationResult.getUserId());
        assertEquals(visitedLocationListTest.get(2),visitedLocationResult);
        assertTrue(userTest2.getVisitedLocations().size() > 0);
        assertEquals(-118.922008D, visitedLocationResult.getLocation().getLongitude());
        assertEquals(35.817595D, visitedLocationResult.getLocation().getLatitude());
        verify(internalUserMapDAO,times(1)).getUser(anyString());
    }

    @Test
    public void getUserLocationTest_whenUserNotExist_thenThrowUserNotFoundException() {
        //GIVEN
        User userTest2 = new User(UUID.randomUUID(), "jona", "000", "jona@tourGuide.com");
        when(internalUserMapDAO.getUser(anyString())).thenReturn(null);
        //WHEN
        //THEN
        assertThrows(UserNotFoundException.class, () -> tourGuideClientServiceTest.getUserLocation("Unknown"));
        verify(internalUserMapDAO,times(1)).getUser(anyString());
    }

}
