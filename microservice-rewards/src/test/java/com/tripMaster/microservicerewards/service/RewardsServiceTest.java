package com.tripMaster.microservicerewards.service;

import com.tripMaster.microservicerewards.model.*;
import com.tripMaster.microservicerewards.proxies.MicroserviceGpsProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rewardCentral.RewardCentral;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Class that test the {@link RewardsServiceImpl}
 *
 * @author Christine Duarte
 */
@ExtendWith(MockitoExtension.class)
public class RewardsServiceTest {

    private RewardsServiceImpl rewardsServiceImplTest;

    @Mock
    private RewardCentral rewardsCentralMock;

    @Mock
    private MicroserviceGpsProxy microserviceGpsProxyMock;

    @BeforeEach
    public void setUpPerTest() {
        rewardsServiceImplTest = new RewardsServiceImpl(rewardsCentralMock, microserviceGpsProxyMock);
    }


    private void addToVisitedLocations(VisitedLocation visitedLocation, User user) {
        List<VisitedLocation> visitedLocations = user.getVisitedLocations();
        visitedLocations.add(visitedLocation);
    }

    @Test
    public void calculateRewardsTest_whenVisitedLocationNearAttraction_thenAddRewardToListRewards(){
        //GIVEN
        List<Attraction> attractions = new ArrayList();
        attractions.add(new Attraction("Disneyland", "Anaheim", "CA", 33.817595D, -117.922008D));
        attractions.add(new Attraction("Jackson Hole", "Jackson Hole", "WY", 43.582767D, -110.821999D));
        attractions.add(new Attraction("Mojave National Preserve", "Kelso", "CA", 35.141689D, -115.510399D));

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), new Location(33.817595D,-117.922008D),new Date());

        when(microserviceGpsProxyMock.getAttractions()).thenReturn(attractions);
        when(rewardsCentralMock.getAttractionRewardPoints(any(UUID.class),any(UUID.class))).thenReturn(200);
        //WHEN
        addToVisitedLocations(visitedLocation,user);
        rewardsServiceImplTest.calculateRewards(user);
        List<UserReward> userRewards = user.getUserRewards();
        //THEN
        assertTrue(userRewards.size() > 0);
        assertEquals(user.getUserId(),userRewards.get(0).getVisitedLocation().getUserId());
        assertEquals(200,userRewards.get(0).getRewardPoints());
        verify(microserviceGpsProxyMock,times(1)).getAttractions();
        verify(rewardsCentralMock,times(1)).getAttractionRewardPoints(any(UUID.class),any(UUID.class));
    }

    @Test
    public void calculateRewardsTest_whenVisitedLocationIsFarAttraction_thenAddRewardToListRewards(){
        //GIVEN
        List<Attraction> attractions = new ArrayList();
        attractions.add(new Attraction("Disneyland", "Anaheim", "CA", 33.817595D, -117.922008D));
        attractions.add(new Attraction("Jackson Hole", "Jackson Hole", "WY", 43.582767D, -110.821999D));
        attractions.add(new Attraction("Mojave National Preserve", "Kelso", "CA", 35.141689D, -115.510399D));

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        //postion Gps Disneyland Paris
        VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), new Location(48.871900D,2.776623D),new Date());

        when(microserviceGpsProxyMock.getAttractions()).thenReturn(attractions);
        //WHEN
        addToVisitedLocations(visitedLocation,user);
        rewardsServiceImplTest.calculateRewards(user);
        List<UserReward> userRewards = user.getUserRewards();
        //THEN
        assertTrue(user.getUserRewards().isEmpty());
        assertEquals(48.871900D,user.getVisitedLocations().get(0).getLocation().getLatitude());
        assertEquals(2.776623D,user.getVisitedLocations().get(0).getLocation().getLongitude());
        verify(microserviceGpsProxyMock,times(1)).getAttractions();

    }
}
