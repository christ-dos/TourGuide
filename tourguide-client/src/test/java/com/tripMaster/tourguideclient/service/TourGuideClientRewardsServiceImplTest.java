package com.tripMaster.tourguideclient.service;

import com.tripMaster.tourguideclient.DAO.InternalUserMapDAO;
import com.tripMaster.tourguideclient.exception.UserNotFoundException;
import com.tripMaster.tourguideclient.helper.InternalTestHelper;
import com.tripMaster.tourguideclient.model.*;
import com.tripMaster.tourguideclient.proxies.MicroserviceRewardsProxy;
import com.tripMaster.tourguideclient.proxies.MicroserviceTripPricerProxy;
import com.tripMaster.tourguideclient.proxies.MicroserviceUserGpsProxy;
import com.tripMaster.tourguideclient.utils.Tracker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TourGuideClientRewardsServiceImplTest {
    private TourGuideClientRewardsServiceImpl tourGuideClientRewardsService;

    private TourGuideClientServiceImpl tourGuideClientService;

    @Mock
    private MicroserviceRewardsProxy microserviceRewardsProxyMock;

    @Mock
    private MicroserviceUserGpsProxy microserviceUserGpsProxyMock;

    @Mock
    private InternalUserMapDAO internalUserMapDAOMock;

    @Mock
    private MicroserviceTripPricerProxy microserviceTripPricerProxyMock;

    private Tracker tracker = new Tracker(tourGuideClientService);


    private User userTest;

    private List<Attraction> attractions;

    @BeforeEach
    public void setUpPerTest() {
        tourGuideClientRewardsService = new TourGuideClientRewardsServiceImpl(microserviceRewardsProxyMock, microserviceUserGpsProxyMock, internalUserMapDAOMock);
        userTest = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        attractions = new ArrayList();
        attractions.add(new Attraction("Disneyland", "Anaheim", "CA", 33.817595D, -117.922008D));
        attractions.add(new Attraction("Jackson Hole", "Jackson Hole", "WY", 43.582767D, -110.821999D));
        attractions.add(new Attraction("Mojave National Preserve", "Kelso", "CA", 35.141689D, -115.510399D));
        attractions.add(new Attraction("Pasteis de Belem", "Lisbon", "Portugal", 38.697417D, -9.203334D));

    }

    @Test
    public void calculateRewardsTest_whenUserHasTwoVisitedLocationsAndTwoAttractionsIsNear_thenUserRewardsAdded() {
        //GIVEN
        VisitedLocation visitedLocationMock = new VisitedLocation(userTest.getUserId(), new Location(33.817595D, -116.922008D), new Date());
        VisitedLocation visitedLocationMock1 = new VisitedLocation(userTest.getUserId(), new Location(34.817595D, -117.922008D), new Date());

        CopyOnWriteArrayList<VisitedLocation> visitedLocations = new CopyOnWriteArrayList<>(Arrays.asList(visitedLocationMock,visitedLocationMock1));
        userTest.setVisitedLocations(visitedLocations);

        InternalTestHelper.setInternalUserNumber(0);

        List<VisitedLocation> visitedLocationsUserTest = userTest.getVisitedLocations();
        when(microserviceUserGpsProxyMock.getAttractions()).thenReturn(attractions);
        //WHEN
        tourGuideClientRewardsService.setProximityBuffer(150);
        tourGuideClientRewardsService.calculateRewards(userTest);
        List<UserReward> userRewards = userTest.getUserRewards();
        //THEN
        tracker.stopTracking();

        assertEquals(2, visitedLocationsUserTest.size());
        assertTrue(userRewards.size() > 0);
        assertEquals(2, userRewards.size());
        assertEquals("Disneyland", userTest.getUserRewards().get(0).getAttraction().getAttractionName());
        assertNotNull(userTest.getUserRewards().get(0).getRewardPoints());
        verify(microserviceUserGpsProxyMock, times(1)).getAttractions();
    }

    @Test
    public void calculateRewardsTest_whenVisitedLocationIsFarOfAttractions_thenUserHasNoRewardsAdded() {
        //GIVEN
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        //Position Gps Disneyland Paris
        VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), new Location(48.871900D, 2.776623D), new Date());
        List<VisitedLocation> visitedLocations = userTest.getVisitedLocations();
        when(microserviceUserGpsProxyMock.getAttractions()).thenReturn(attractions);

        InternalTestHelper.setInternalUserNumber(0);
        //WHEN
        visitedLocations.add(visitedLocation);
        tourGuideClientRewardsService.calculateRewards(user);
        List<UserReward> userRewards = user.getUserRewards();
        //THEN
        tracker.stopTracking();

        assertTrue(user.getUserRewards().isEmpty());
        assertEquals(user.getUserId(), visitedLocations.get(0).getUserId());
        assertEquals(48.871900D, visitedLocations.get(0).getLocation().getLatitude());
        assertEquals(2.776623D, visitedLocations.get(0).getLocation().getLongitude());
        verify(microserviceUserGpsProxyMock, times(1)).getAttractions();

    }

    @Test
    public void getUserRewardsTest_whenUserHasTwoUserRewards_thenReturnListWithTwoElements() {
        //GIVEN
        Attraction attraction1 = new Attraction("Disneyland", "Anaheim", "CA", 33.817595D, -117.922008D);
        Attraction attraction2 = new Attraction("Jackson Hole", "Jackson Hole", "WY", 43.582767D, -110.821999D);

        VisitedLocation visitedLocationMock1 = new VisitedLocation(userTest.getUserId(), new Location(33.817595D, -116.922008D), new Date());
        VisitedLocation visitedLocationMock2 = new VisitedLocation(userTest.getUserId(), new Location(34.817595D, -117.922008D), new Date());

        List<UserReward> rewards = Arrays.asList(
                new UserReward(visitedLocationMock1, attraction1, 200),
                new UserReward(visitedLocationMock2, attraction2, 500));
        userTest.setUserRewards(rewards);
        when(internalUserMapDAOMock.getUser(anyString())).thenReturn(userTest);

        InternalTestHelper.setInternalUserNumber(0);
        //WHEN
        List<UserReward> rewardsResult = tourGuideClientRewardsService.getUserRewards("jon");
        //THEN
        tracker.stopTracking();

        assertEquals(2, rewardsResult.size());
        assertEquals("Disneyland", rewardsResult.get(0).getAttraction().getAttractionName());
    }

    @Test
    public void getUserRewardsTest_whenUserHasNoUserRewards_thenReturnEmptyList() {
        //GIVEN
        when(internalUserMapDAOMock.getUser(anyString())).thenReturn(userTest);
        //WHEN
        List<UserReward> rewardsResult = tourGuideClientRewardsService.getUserRewards("jon");
        //THEN
        assertTrue(rewardsResult.isEmpty());
    }

    @Test
    public void getUserRewardsTest_whenUserNotExist_thenThrowUserNotFoundexception(){
        when(internalUserMapDAOMock.getUser(anyString())).thenReturn(null);
        //WHEN
        //THEN
        tracker.stopTracking();

        assertThrows(UserNotFoundException.class, () -> tourGuideClientRewardsService.getUserRewards("Unknown"));
        verify(internalUserMapDAOMock, times(1)).getUser(anyString());


    }

    @Test
    public void isWithinAttractionProximityTest_whenDistanceLessThanAttractionProximityRange_thenReturnTrue() {
        //GIVEN
        tourGuideClientRewardsService.setAttractionProximityRange(200);
        Attraction attraction = new Attraction("Disneyland", "Anaheim", "CA", 33.817595D, -116.922008D);
        VisitedLocation visitedLocation = new VisitedLocation(userTest.getUserId(), new Location(33.817595D, -116.922008D), new Date());
        //WHEN
        Boolean result = tourGuideClientRewardsService.isWithinAttractionProximity(attraction, visitedLocation.getLocation());
        //THEN
        tracker.stopTracking();

        assertTrue(result);
    }

    @Test
    public void isWithinAttractionProximityTest_whenDistanceGreaterThanAttractionProximityRange_thenReturnFalse() {
        //GIVEN
        tourGuideClientRewardsService.setAttractionProximityRange(200);
        Attraction attraction = new Attraction("Belem", "Lisbon", "Portugal", -1.455755D, -48.490180);
        VisitedLocation visitedLocation = new VisitedLocation(userTest.getUserId(), new Location(33.817595D, -116.922008D), new Date());
        //WHEN
        Boolean result = tourGuideClientRewardsService.isWithinAttractionProximity(attraction, visitedLocation.getLocation());
        //THEN
        assertFalse(result);
    }

    @Test
    public void nearAllAttractionsTest() {
        //GIVEN
        CopyOnWriteArrayList<VisitedLocation> visitedLocationsTest = new CopyOnWriteArrayList<>(Arrays.asList(
                new VisitedLocation(userTest.getUserId(), new Location(33.817595D, -116.922008D), new Date()),
                new VisitedLocation(userTest.getUserId(), new Location(34.817595D, -117.922008D), new Date())
        ));
        userTest.setVisitedLocations(visitedLocationsTest);

        tourGuideClientRewardsService.setProximityBuffer(Integer.MAX_VALUE);
        when(internalUserMapDAOMock.getUser(anyString())).thenReturn(userTest);
        when(microserviceUserGpsProxyMock.getAttractions()).thenReturn(attractions);

        InternalTestHelper.setInternalUserNumber(0);
        //WHEN
        tourGuideClientRewardsService.calculateRewards(userTest);
        List<UserReward> userRewardsResult = tourGuideClientRewardsService.getUserRewards(userTest.getUserName());
        //THEN
        tracker.stopTracking();

        assertEquals(attractions.size(), userRewardsResult.size());
    }

    @Test
    public void userGetRewardsTest_whenVisitedLocationIsNearOfOnlyOneAttractionInListAttractions_thenReturnOneUserRewards() {
        //GIVEN
        TourGuideClientServiceImpl tourGuideClientService = new TourGuideClientServiceImpl(microserviceRewardsProxyMock,
                microserviceUserGpsProxyMock, internalUserMapDAOMock, tourGuideClientRewardsService, microserviceTripPricerProxyMock);
        //location  in Monastere dos Jeronimos located in Belem/Lisbon
        VisitedLocation visitedLocation = new VisitedLocation(userTest.getUserId(), new Location(38.697500D, -9.206667D), new Date());

        Attraction attraction = new Attraction("Pasteis Belem", "Lisbon", "Portugal", 38.697417D, -9.203334D);
        when(microserviceUserGpsProxyMock.getAttractions()).thenReturn(attractions);
        when(microserviceUserGpsProxyMock.trackUserLocation(any(UUID.class))).thenReturn(visitedLocation);

        InternalTestHelper.setInternalUserNumber(0);
        //WHEN
        tourGuideClientService.trackUserLocation(userTest);
        tourGuideClientRewardsService.calculateRewards(userTest);
        List<UserReward> userRewards = userTest.getUserRewards();
        //THEN
        tracker.stopTracking();
        assertTrue(userRewards.size() == 1);
    }

}
