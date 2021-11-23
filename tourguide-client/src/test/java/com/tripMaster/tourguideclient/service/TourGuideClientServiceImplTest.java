package com.tripMaster.tourguideclient.service;

import com.tripMaster.tourguideclient.DAO.InternalUserMapDAO;
import com.tripMaster.tourguideclient.exception.UserNotFoundException;
import com.tripMaster.tourguideclient.exception.UserRewardsNotFoundException;
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

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Class that test {@link TourGuideClientServiceImpl}
 *
 * @author Christine Duarte
 */
@ExtendWith(MockitoExtension.class)
public class TourGuideClientServiceImplTest {

    private TourGuideClientServiceImpl tourGuideClientServiceTest;

    @Mock
    private MicroserviceUserGpsProxy microserviceUserGpsProxyMock;
    @Mock
    private InternalUserMapDAO internalUserMapDAOMock;
    @Mock
    private TourGuideClientRewardsServiceImpl tourGuideClientRewardsServiceImplMock;
    @Mock
    private MicroserviceTripPricerProxy microserviceTripPricerProxyMock;
    @Mock
    private MicroserviceRewardsProxy microserviceRewardsProxyMock;
    @Mock
    private Tracker trackerMock;

    private User userTest;

    @BeforeEach
    public void setUpPerTest() {
        tourGuideClientServiceTest = new TourGuideClientServiceImpl(microserviceRewardsProxyMock,
                microserviceUserGpsProxyMock, internalUserMapDAOMock, tourGuideClientRewardsServiceImplMock, microserviceTripPricerProxyMock);

        userTest = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    }

    @Test
    public void trackUserLocationTest() {
        //GIVEN
        VisitedLocation visitedLocationMock = new com.tripMaster.tourguideclient.model.VisitedLocation(userTest.getUserId(), new Location(33.817595D, -116.922008D), new Date());
        when(microserviceUserGpsProxyMock.trackUserLocation(any(UUID.class))).thenReturn(visitedLocationMock);
        //WHEN
        VisitedLocation visitedLocationResult = tourGuideClientServiceTest.trackUserLocation(userTest).join();
        //THEN
        tourGuideClientServiceTest.tracker.stopTracking();

        assertTrue(visitedLocationResult.getUserId() == userTest.getUserId());
        assertEquals(-116.922008D, visitedLocationResult.getLocation().getLongitude());
        assertEquals(33.817595D, visitedLocationResult.getLocation().getLatitude());

        verify(microserviceUserGpsProxyMock, times(1)).trackUserLocation(any(UUID.class));
    }

    @Test
    public void addUserTest_thenVerifyUserWasAdded() {
        //GIVEN
        doNothing().when(internalUserMapDAOMock).addUser(any(User.class));
        when(internalUserMapDAOMock.getUser(anyString())).thenReturn(userTest);
        //WHEN
        tourGuideClientServiceTest.addUser(userTest);

        User retrivedUserTest = tourGuideClientServiceTest.getUser("jon");
        //THEN
        tourGuideClientServiceTest.tracker.stopTracking();

        assertEquals(userTest, retrivedUserTest);
        verify(internalUserMapDAOMock, times(1)).getUser(anyString());
        verify(internalUserMapDAOMock, times(1)).addUser(any(User.class));
    }

    @Test
    public void getAllUsersTest_thenReturnListWithTwoUsersWhichContainUserAndUserOne() {
        //GIVEN
        List<User> usersListTest = Arrays.asList(
                new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com"),
                new User(UUID.randomUUID(), "jon1", "000", "jon2@tourGuide.com")
        );
        when(internalUserMapDAOMock.getAllUsers()).thenReturn(usersListTest);
        //WHEN
        List<User> allUsers = tourGuideClientServiceTest.getAllUsers();
        //THEN
        tourGuideClientServiceTest.tracker.stopTracking();

        assertTrue(allUsers.size() > 0);
        assertTrue(allUsers.containsAll(usersListTest));
    }

    @Test
    public void getUserTest_whenUserExistAndUserNameIsJon_thenReturnUser() {
        //GIVEN
        when(internalUserMapDAOMock.getUser(anyString())).thenReturn(userTest);
        //WHEN
        User userResult = tourGuideClientServiceTest.getUser("jon");
        //THEN
        tourGuideClientServiceTest.tracker.stopTracking();

        assertNotNull(userResult);
        assertEquals(userTest.getUserId(), userResult.getUserId());
        assertEquals(userTest.getEmailAddress(), userResult.getEmailAddress());
        verify(internalUserMapDAOMock, times(1)).getUser(anyString());
    }

    @Test
    public void getUserTest_whenUserNotExist_thenThrowUserNotFoundException() {
        //GIVEN
        when(internalUserMapDAOMock.getUser(anyString())).thenReturn(null);
        //WHEN
        //THEN
        tourGuideClientServiceTest.tracker.stopTracking();

        assertThrows(UserNotFoundException.class, () -> tourGuideClientServiceTest.getUser("Unknown"));
        verify(internalUserMapDAOMock, times(1)).getUser(anyString());

    }

    @Test
    public void getUserLocationTest_whenVisitedLocationsListIsEmpty_thenCallMethodTrackUserLocation() {
        //GIVEN
        VisitedLocation visitedLocationMock = new VisitedLocation(userTest.getUserId(), new Location(33.817595D, -116.922008D), new Date());
        CopyOnWriteArrayList<VisitedLocation> emptyListTest = new CopyOnWriteArrayList<>();
        userTest.setVisitedLocations(emptyListTest);

        when(internalUserMapDAOMock.getUser(anyString())).thenReturn(userTest);
        when(microserviceUserGpsProxyMock.trackUserLocation(userTest.getUserId())).thenReturn(visitedLocationMock);
        //WHEN
        assertTrue(userTest.getVisitedLocations().isEmpty());
        VisitedLocation visitedLocationResult = tourGuideClientServiceTest.getUserLocation(userTest.getUserName());
        //THEN
        tourGuideClientServiceTest.tracker.stopTracking();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(visitedLocationResult.getUserId().equals(userTest.getUserId()));
        assertTrue(userTest.getVisitedLocations().size() > 0);
        assertEquals(-116.922008D, visitedLocationResult.getLocation().getLongitude());
        assertEquals(33.817595D, visitedLocationResult.getLocation().getLatitude());

        verify(microserviceUserGpsProxyMock, times(1)).trackUserLocation(any(UUID.class));
        verify(internalUserMapDAOMock, times(1)).getUser(anyString());
    }

    @Test
    public void getUserLocationTest_whenVisitedLocationsListIsNotEmpty_thenGetLastVisitedLocation() {
        //GIVEN
        User userTest2 = new User(UUID.randomUUID(), "jona", "000", "jona@tourGuide.com");

        CopyOnWriteArrayList<VisitedLocation> visitedLocationListTest = new CopyOnWriteArrayList<>(Arrays.asList(
                new VisitedLocation(userTest2.getUserId(), new Location(33.817595D, -116.922008D), new Date()),
                new VisitedLocation(userTest2.getUserId(), new Location(34.817595D, -117.922008D), new Date()),
                new VisitedLocation(userTest2.getUserId(), new Location(35.817595D, -118.922008D), new Date())
        ));
        userTest2.setVisitedLocations(visitedLocationListTest);
        when(internalUserMapDAOMock.getUser(anyString())).thenReturn(userTest2);
        //WHEN
        InternalTestHelper.setInternalUserNumber(0);
        VisitedLocation visitedLocationResult = tourGuideClientServiceTest.getUserLocation(userTest2.getUserName());
        //THEN
        try {
            TimeUnit.MILLISECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tourGuideClientServiceTest.tracker.stopTracking();

        assertEquals(userTest2.getUserId(), visitedLocationResult.getUserId());
        assertEquals(visitedLocationListTest.get(2), visitedLocationResult);
        assertTrue(userTest2.getVisitedLocations().size() > 0);
        assertEquals(-118.922008D, visitedLocationResult.getLocation().getLongitude());
        assertEquals(35.817595D, visitedLocationResult.getLocation().getLatitude());
        verify(internalUserMapDAOMock, times(1)).getUser(anyString());
    }

    @Test
    public void getUserLocationTest_whenUserNotExist_thenThrowUserNotFoundException() {
        //GIVEN
        when(internalUserMapDAOMock.getUser(anyString())).thenReturn(null);
        //WHEN
        //THEN
        tourGuideClientServiceTest.tracker.stopTracking();

        assertThrows(UserNotFoundException.class, () -> tourGuideClientServiceTest.getUserLocation("Unknown"));
        verify(internalUserMapDAOMock, times(1)).getUser(anyString());
    }

    @Test
    public void getAllCurrentLocationsTest_whenUserListNotEmpty_thenReturnListOfUsers() {
        //GIVEN
        List<User> usersListTest = Arrays.asList(
                new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com"),
                new User(UUID.randomUUID(), "jon1", "000", "jon2@tourGuide.com")
        );
        CopyOnWriteArrayList<VisitedLocation> visitedLocationListTestUserJon = new CopyOnWriteArrayList<>(Arrays.asList(
                new VisitedLocation(usersListTest.get(0).getUserId(), new Location(33.817595D, -116.922008D), new Date()),
                new VisitedLocation(usersListTest.get(0).getUserId(), new Location(34.817595D, -117.922008D), new Date())
        ));
        CopyOnWriteArrayList<VisitedLocation> visitedLocationListTestUserJon1 = new CopyOnWriteArrayList<>(Arrays.asList(
                new VisitedLocation(usersListTest.get(1).getUserId(), new Location(35.817595D, -118.922008D), new Date()),
                new VisitedLocation(usersListTest.get(1).getUserId(), new Location(36.817595D, -119.922008D), new Date())
        ));
        usersListTest.get(0).setVisitedLocations(visitedLocationListTestUserJon);
        usersListTest.get(1).setVisitedLocations(visitedLocationListTestUserJon1);

        when(internalUserMapDAOMock.getAllUsers()).thenReturn(usersListTest);
        //WHEN
        List<UserCurrentLocation> currentLocations = tourGuideClientServiceTest.getAllCurrentLocations();
        //THEN
        assertTrue(currentLocations.size() == 2);
        assertEquals(usersListTest.get(0).getUserId(), currentLocations.get(0).getUserId());
        assertEquals(-117.922008, currentLocations.get(0).getLocation().getLongitude());
        assertEquals(34.817595, currentLocations.get(0).getLocation().getLatitude());
    }

    @Test
    public void getAllCurrentLocationsTest_whenUserListIsEmpty_thenReturnEmptyList() {
        //GIVEN
        List<User> usersListEmpty = new ArrayList<>();
        when(internalUserMapDAOMock.getAllUsers()).thenReturn(usersListEmpty);
        //WHEN
        List<UserCurrentLocation> currentLocations = tourGuideClientServiceTest.getAllCurrentLocations();
        //THEN
        assertTrue(currentLocations.isEmpty());
    }

    @Test
    public void getTripDealsTest_whenUserExistAndUserRewardsListIsNotEmpty_thenReturnListProviders() {
        //GIVEN
        UserPreferences userPreferences = new UserPreferences(200, 5, 2, 1);
        Attraction attraction1 = new Attraction("Disneyland", "Anaheim", "CA", 33.817595D, -117.922008D);
        Attraction attraction2 = new Attraction("Jackson Hole", "Jackson Hole", "WY", 43.582767D, -110.821999D);

        VisitedLocation visitedLocation1 = new VisitedLocation(userTest.getUserId(), new Location(33.817595D, -116.922008D), new Date());
        VisitedLocation visitedLocation2 = new VisitedLocation(userTest.getUserId(), new Location(34.817595D, -117.922008D), new Date());

        List<UserReward> rewardsTest = Arrays.asList(
                new UserReward(visitedLocation1, attraction1, 200),
                new UserReward(visitedLocation2, attraction2, 500)
        );

        List<Provider> providersTest = Arrays.asList(
                new Provider(UUID.randomUUID(), "Holiday Travels", 200D),
                new Provider(UUID.randomUUID(), "FlyAway Trips", 150D),
                new Provider(UUID.randomUUID(), "Sunny Days", 500D)
        );

        when(internalUserMapDAOMock.getUser(anyString())).thenReturn(userTest);
        when(internalUserMapDAOMock.getTripPricerApiKey()).thenReturn("apiKey");
        userTest.setUserRewards(rewardsTest);

        when(microserviceTripPricerProxyMock.
                getProviders(anyString(), any(UUID.class), anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(providersTest);
        //WHEN
        userTest.setUserPreferences(userPreferences);
        List<Provider> providersResult = tourGuideClientServiceTest.getTripDeals("jon");
        //THEN
        tourGuideClientServiceTest.tracker.stopTracking();

        assertEquals(3, providersResult.size());
        assertEquals("Holiday Travels", providersResult.get(0).getName());
        assertEquals(200, providersResult.get(0).getPrice());
        assertEquals(200, userTest.getUserRewards().get(0).getRewardPoints());
        assertEquals(700, userTest.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum());
    }

    @Test
    public void getTripDealsTest_whenUserNotFound_thenReturnUserNotFoundException() {
        //GIVEN
        when(internalUserMapDAOMock.getUser(anyString())).thenReturn(null);
        //WHEN
        //THEN
        tourGuideClientServiceTest.tracker.stopTracking();

        assertThrows(UserNotFoundException.class, () -> tourGuideClientServiceTest.getTripDeals("Unknown"));
    }

    @Test
    public void getTripDealsTest_whenUserRewardsIsEmpty_thenReturnUserRewardsNotFoundException() {
        //GIVEN
        List<UserReward> emptyList = new ArrayList<>();
        when(internalUserMapDAOMock.getUser(anyString())).thenReturn(userTest);
        userTest.setUserRewards(emptyList);
        //WHEN
        //THEN
        tourGuideClientServiceTest.tracker.stopTracking();

        assertThrows(UserRewardsNotFoundException.class, () -> tourGuideClientServiceTest.getTripDeals("jon"));
    }

    @Test
    public void getNearByAttractionsTest_whenThreeAttractionsHaveDistanceLessThanAttractionProximityRange_thenReturnListAttractionWithThreeAttractionsRetrievedByNewProximityRangeSetWithAverageDistanceCalculate() {
        //GIVEN
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), new Location(33.817595D, -117.922008D), new Date());

        List<Attraction> attractions = new ArrayList();
        attractions.add(new Attraction("Disneyland", "Anaheim", "CA", 33.817595D, -117.922008D));
        attractions.add(new Attraction("Mojave National Preserve", "Kelso", "CA", 35.141689D, -115.510399D));
        attractions.add(new Attraction("Belem", "Lisbon", "Portugal", -1.455755D, -48.490180));

        when(microserviceUserGpsProxyMock.getAttractionsByAverageDistance(anyDouble(), anyDouble())).thenReturn(attractions);
        when(tourGuideClientRewardsServiceImplMock.getDistance(any(Attraction.class), any(Location.class)))
                .thenReturn(190D, 150D, 190D, 10D, 10D, 150D, 190D);
        when(microserviceRewardsProxyMock.getRewardsPoints(any(UUID.class), any(UUID.class)))
                .thenReturn(750, 200, 600);
        //WHEN
        InternalTestHelper.setInternalUserNumber(0);
        List<NearByAttraction> attractionsResult = tourGuideClientServiceTest.getNearByAttractions(visitedLocation);
        //THEN
        tourGuideClientServiceTest.tracker.stopTracking();

        //2 attractions are near of average distance of position
        assertEquals(3, attractionsResult.size());
        assertEquals("Disneyland", attractionsResult.get(0).getAttractionName());
        assertEquals(10, attractionsResult.get(0).getDistance());
        assertEquals(190, attractionsResult.get(2).getDistance());
        //stored order ascending
        assertTrue(attractionsResult.get(0).getDistance() < attractionsResult.get(2).getDistance());

        verify(microserviceUserGpsProxyMock, times(1)).getAttractionsByAverageDistance(anyDouble(), anyDouble());
        verify(tourGuideClientRewardsServiceImplMock, times(7)).getDistance(any(Location.class), any(Location.class));
        verify(microserviceRewardsProxyMock, times(3)).getRewardsPoints(any(UUID.class), any(UUID.class));

    }

    @Test
    public void getNearByAttractionsTest_whenSixAttractionsHaveDistanceLessThanAttractionProximityRange_thenReturnListAttractionWith5Attractions() {
        //GIVEN
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), new Location(33.817595D, -117.922008D), new Date());

        List<Attraction> attractions = new ArrayList();
        attractions.add(new Attraction("Disneyland", "Anaheim", "CA", 33.817595D, -117.922008D));
        attractions.add(new Attraction("Mojave National Preserve", "Kelso", "CA", 35.141689D, -115.510399D));
        attractions.add(new Attraction("Jackson Hole", "Jackson Hole", "WY", 43.582767D, -110.821999D));
        attractions.add(new Attraction("Disneyland", "Paris", "France", 48.871900D, 2.776623D));
        attractions.add(new Attraction("Belem", "Lisbon", "Portugal", -1.455755D, -48.490180));
        attractions.add(new Attraction("Himalaya", "Namche Barwa", "Tibet", 55.742793D, 37.615401D));

        when(microserviceUserGpsProxyMock.getAttractionsByAverageDistance(anyDouble(), anyDouble())).thenReturn(attractions);
        when(tourGuideClientRewardsServiceImplMock.getDistance(any(Attraction.class), any(Location.class)))
                .thenReturn(190D, 150D, 190D, 130D, 190D, 50D, 190D, 30D, 190D, 10D, 10D, 30D, 50D, 130D, 150D, 190D);
        when(microserviceRewardsProxyMock.getRewardsPoints(any(UUID.class), any(UUID.class)))
                .thenReturn(750, 200, 600);
        //WHEN
        InternalTestHelper.setInternalUserNumber(0);
        List<NearByAttraction> attractionsResult = tourGuideClientServiceTest.getNearByAttractions(visitedLocation);
        //THEN
        tourGuideClientServiceTest.tracker.stopTracking();

        //5 attractions are near of average distance of position
        assertEquals(5, attractionsResult.size());
        assertEquals("Disneyland", attractionsResult.get(0).getAttractionName());
        assertEquals(10, attractionsResult.get(0).getDistance());
        //we take so five NearbyAttraction the sixth with distance 150 not stored
        assertEquals(150, attractionsResult.get(4).getDistance());
        //stored order ascending
        assertTrue(attractionsResult.get(0).getDistance() < attractionsResult.get(4).getDistance());

        verify(microserviceUserGpsProxyMock, times(1)).getAttractionsByAverageDistance(anyDouble(), anyDouble());
        verify(tourGuideClientRewardsServiceImplMock, times(15)).getDistance(any(Location.class), any(Location.class));
        verify(microserviceRewardsProxyMock, times(5)).getRewardsPoints(any(UUID.class), any(UUID.class));
    }

    @Test
    public void getNearByAttractionsTest_whenNoAttractionsHaveDistanceLessThanAttractionProximityRange_thenReturnListWithFiveAttractionsNearOfAverageDistance() {
        //GIVEN
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), new Location(33.817595D, -117.922008D), new Date());
        List<Attraction> attractions = new ArrayList();
        attractions.add(new Attraction("Disneyland", "Anaheim", "CA", 33.817595D, -117.922008D));
        attractions.add(new Attraction("Mojave National Preserve", "Kelso", "CA", 35.141689D, -115.510399D));
        attractions.add(new Attraction("Jackson Hole", "Jackson Hole", "WY", 43.582767D, -110.821999D));
        attractions.add(new Attraction("Disneyland", "Paris", "France", 48.871900D, 2.776623D));
        attractions.add(new Attraction("Belem", "Lisbon", "Portugal", -1.455755D, -48.490180));
        attractions.add(new Attraction("Himalaya", "Namche Barwa", "Tibet", 55.742793D, 37.615401D));

        when(microserviceUserGpsProxyMock.getAttractionsByAverageDistance(anyDouble(), anyDouble())).thenReturn(attractions);
        when(tourGuideClientRewardsServiceImplMock.getDistance(any(Attraction.class), any(Location.class)))
                .thenReturn(1900D, 1500D, 1900D, 1300D, 1900D, 500D, 1900D, 300D, 1900D, 250D, 250D, 300D, 500D, 1300D, 1500D, 1900D);
        when(microserviceRewardsProxyMock.getRewardsPoints(any(UUID.class), any(UUID.class)))
                .thenReturn(150, 20, 300, 600, 300);
        //WHEN
        InternalTestHelper.setInternalUserNumber(0);
        List<NearByAttraction> attractionsResult = tourGuideClientServiceTest.getNearByAttractions(visitedLocation);
        //THEN
        tourGuideClientServiceTest.tracker.stopTracking();
        //five attraction are near of position
        assertEquals(5, attractionsResult.size());
        //stored order ascending
        assertEquals(250, attractionsResult.get(0).getDistance());
        assertEquals(1500, attractionsResult.get(4).getDistance());
        assertTrue(attractionsResult.get(0).getDistance() < attractionsResult.get(4).getDistance());

        verify(microserviceUserGpsProxyMock, times(1)).getAttractionsByAverageDistance(anyDouble(), anyDouble());
        verify(tourGuideClientRewardsServiceImplMock, times(15)).getDistance(any(Location.class), any(Location.class));

        verify(microserviceRewardsProxyMock, times(5)).getRewardsPoints(any(UUID.class), any(UUID.class));
    }

    @Test
    public void addToVisitedLocation() {
        //GIVEN
        VisitedLocation visitedLocation = new VisitedLocation(userTest.getUserId(), new Location(33.817595D, -117.922008D), new Date());
        //WHEN
        tourGuideClientServiceTest.addToVisitedLocations(visitedLocation, userTest);
        List<VisitedLocation> visitedLocations = userTest.getVisitedLocations();
        //THEN
        tourGuideClientServiceTest.tracker.stopTracking();

        assertNotNull(visitedLocations);
        assertEquals(userTest.getUserId(), visitedLocation.getUserId());
        assertEquals(visitedLocation.getLocation().getLatitude(), visitedLocations.get(0).getLocation().getLatitude());
        assertEquals(visitedLocation.getLocation().getLongitude(), visitedLocations.get(0).getLocation().getLongitude());
    }
}
