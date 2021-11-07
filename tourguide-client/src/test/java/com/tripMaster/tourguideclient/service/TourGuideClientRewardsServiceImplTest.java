package com.tripMaster.tourguideclient.service;

import com.tripMaster.tourguideclient.DAO.InternalUserMapDAO;
import com.tripMaster.tourguideclient.model.*;
import com.tripMaster.tourguideclient.proxies.MicroserviceRewardsProxy;
import com.tripMaster.tourguideclient.proxies.MicroserviceUserGpsProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TourGuideClientRewardsServiceImplTest {
    private TourGuideClientRewardsServiceImpl tourGuideClientRewardsService;

    @Mock
    private MicroserviceRewardsProxy microserviceRewardsProxy;

    @Mock
    private MicroserviceUserGpsProxy microserviceUserGpsProxy;

    @Mock
    private InternalUserMapDAO internalUserMapDAO;

    private User userTest;

    @BeforeEach
    public void setUpPerTest() {
        tourGuideClientRewardsService = new TourGuideClientRewardsServiceImpl(microserviceRewardsProxy, microserviceUserGpsProxy, internalUserMapDAO);
        userTest = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");


    }

    @Test
    public void calculateRewardsTest_whenUserVisitedLocationAreTwoAndAttractionsIsNear_thenUserRewardsAdded() {
        //GIVEN
        VisitedLocation visitedLocationMock = new VisitedLocation(userTest.getUserId(), new Location(33.817595D, -116.922008D), new Date());
        VisitedLocation visitedLocationMock1 = new VisitedLocation(userTest.getUserId(), new Location(34.817595D, -117.922008D), new Date());

        userTest.setVisitedLocations(Arrays.asList(visitedLocationMock, visitedLocationMock1));
        List<VisitedLocation> visitedLocations = userTest.getVisitedLocations();
        List<Attraction> attractions = new ArrayList();
        attractions.add(new Attraction("Disneyland", "Anaheim", "CA", 33.817595D, -117.922008D));
        attractions.add(new Attraction("Jackson Hole", "Jackson Hole", "WY", 43.582767D, -110.821999D));
        attractions.add(new Attraction("Mojave National Preserve", "Kelso", "CA", 35.141689D, -115.510399D));
        when(microserviceUserGpsProxy.getAttractions()).thenReturn(attractions);
        //WHEN
        tourGuideClientRewardsService.calculateRewards(userTest);
        //THEN
        assertEquals(2, userTest.getVisitedLocations().size());
        assertEquals(3, userTest.getUserRewards().size());
        assertEquals("Disneyland", userTest.getUserRewards().get(0).getAttraction().getAttractionName());
        assertNotNull(userTest.getUserRewards().get(0).getRewardPoints());
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
        when(internalUserMapDAO.getUser(anyString())).thenReturn(userTest);
        //WHEN
        List<UserReward> rewardsResult = tourGuideClientRewardsService.getUserRewards("jon");
        //THEN
        assertEquals(2,rewardsResult.size());
        assertEquals("Disneyland",rewardsResult.get(0).getAttraction().getAttractionName());
    }

    @Test
    public void getUserRewardsTest_whenUserHasNoUserRewards_thenReturnEmptyList() {
        //GIVEN
        when(internalUserMapDAO.getUser(anyString())).thenReturn(userTest);
        //WHEN
        List<UserReward> rewardsResult = tourGuideClientRewardsService.getUserRewards("jon");
        //THEN
        assertTrue(rewardsResult.isEmpty());
    }
}
