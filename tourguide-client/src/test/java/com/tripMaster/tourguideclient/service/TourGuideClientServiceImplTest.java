package com.tripMaster.tourguideclient.service;

import com.tripMaster.tourguideclient.DAO.InternalUserMapDAO;
import com.tripMaster.tourguideclient.exception.UserNotFoundException;
import com.tripMaster.tourguideclient.helper.InternalTestHelper;
import com.tripMaster.tourguideclient.model.Attraction;
import com.tripMaster.tourguideclient.model.Location;
import com.tripMaster.tourguideclient.model.User;
import com.tripMaster.tourguideclient.model.VisitedLocation;
import com.tripMaster.tourguideclient.proxies.MicroserviceTripPricerProxy;
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
    private MicroserviceUserGpsProxy microserviceUserGpsProxy;

    @Mock
    private InternalUserMapDAO internalUserMapDAO;

    @Mock
    private TourGuideClientRewardsServiceImpl tourGuideClientRewardsServiceImpl;

    @Mock
    MicroserviceTripPricerProxy microserviceTripPricerProxy;

    private User userTest;

    @BeforeEach
    public void setUpPerTest() {
        tourGuideClientServiceTest = new TourGuideClientServiceImpl(
                microserviceUserGpsProxy, internalUserMapDAO, tourGuideClientRewardsServiceImpl, microserviceTripPricerProxy);
        userTest = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    }

    @Test
    public void trackUserLocationTest() {
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
        assertEquals(-116.922008D, visitedLocationResult.getLocation().getLongitude());
        assertEquals(33.817595D, visitedLocationResult.getLocation().getLatitude());

        verify(microserviceUserGpsProxy, times(1)).trackUserLocation(any(UUID.class));
        verify(internalUserMapDAO, times(1)).getUser(anyString());
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
        assertEquals(userTest2.getUserId(), visitedLocationResult.getUserId());
        assertEquals(visitedLocationListTest.get(2), visitedLocationResult);
        assertTrue(userTest2.getVisitedLocations().size() > 0);
        assertEquals(-118.922008D, visitedLocationResult.getLocation().getLongitude());
        assertEquals(35.817595D, visitedLocationResult.getLocation().getLatitude());
        verify(internalUserMapDAO, times(1)).getUser(anyString());
    }

    @Test
    public void getUserLocationTest_whenUserNotExist_thenThrowUserNotFoundException() {
        //GIVEN
        User userTest2 = new User(UUID.randomUUID(), "jona", "000", "jona@tourGuide.com");
        when(internalUserMapDAO.getUser(anyString())).thenReturn(null);
        //WHEN
        //THEN
        assertThrows(UserNotFoundException.class, () -> tourGuideClientServiceTest.getUserLocation("Unknown"));
        verify(internalUserMapDAO, times(1)).getUser(anyString());
    }

    @Test
    public void getTripDealsTest() {
        //GIVEN
        //WHEN
        //THEN
    }

    @Test
    public void getNearByAttractionsTest_whenDistanceLessAttractionProximityRange_thenReturnListAttraction() {
        //GIVEN
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), new Location(33.817595D, -117.922008D), new Date());

        List<Attraction> attractions = new ArrayList();
        attractions.add(new Attraction("Disneyland", "Anaheim", "CA", 33.817595D, -117.922008D));
        attractions.add(new Attraction("Jackson Hole", "Jackson Hole", "WY", 43.582767D, -110.821999D));
        attractions.add(new Attraction("Mojave National Preserve", "Kelso", "CA", 35.141689D, -115.510399D));
        when(microserviceUserGpsProxy.getAttractions()).thenReturn(attractions);


        //WHEN
        InternalTestHelper.setInternalUserNumber(0);
        List<Attraction> attractionsResult = tourGuideClientServiceTest.getNearByAttractions(visitedLocation);
        //THEN
        assertEquals(5, attractions.size());
    }
}
