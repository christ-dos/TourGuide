package com.tripMaster.tourguideclient.controller;

import com.tripMaster.tourguideclient.exception.UserNotFoundException;
import com.tripMaster.tourguideclient.model.*;
import com.tripMaster.tourguideclient.service.TourGuideClientRewardsService;
import com.tripMaster.tourguideclient.service.TourGuideClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TourGuideClientController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class TourGuideClientControllerTest {
    @Autowired
    private MockMvc mockMvcUserGps;

    @MockBean
    private TourGuideClientServiceImpl tourGuideClientServiceMock;

    @MockBean
    private TourGuideClientRewardsService tourGuideClientRewardsServiceMock;

    private User userTest;

    @BeforeEach
    public void setupPerTest() {
        userTest = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    }


    @Test
    public void userGpsGetLocationTest_whenUserNameIsJonAndVisitedLocationsListIsNotEmpty_thenReturnVisitedLocationOfJon() throws Exception {
        //GIVEN
        CopyOnWriteArrayList<VisitedLocation> visitedLocationListTest = new CopyOnWriteArrayList<>(Arrays.asList(
                new VisitedLocation(userTest.getUserId(), new Location(33.817595D, -116.922008D), new Date()),
                new VisitedLocation(userTest.getUserId(), new Location(34.817595D, -117.922008D), new Date()),
                new VisitedLocation(userTest.getUserId(), new Location(35.817595D, -118.922008D), new Date())
        ));
        when(tourGuideClientServiceMock.getUserLocation(anyString())).thenReturn(visitedLocationListTest.get(2));
        //WHEN
        userTest.setVisitedLocations(visitedLocationListTest);
        //THEN
        mockMvcUserGps.perform(MockMvcRequestBuilders.get("/getLocation?userName=jon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(String.valueOf(userTest.getUserId()))))
                .andExpect(jsonPath("$.location.longitude", is(-118.922008D)))
                .andExpect(jsonPath("$.location.latitude", is(35.817595D)))
                .andDo(print());
    }

    @Test
    public void userGpsGetLocationTest_whenUserNameIsJonAndVisitedLocationsIsEmpty_thenReturnVisitedLocationOfJonTracked() throws Exception {
        //GIVEN
        VisitedLocation visitedLocationTest = new VisitedLocation(userTest.getUserId(), new Location(33.817595D, -116.922008D), new Date());
        CopyOnWriteArrayList<VisitedLocation> visitedLocationListEmptyTest = new CopyOnWriteArrayList<>();
        when(tourGuideClientServiceMock.getUserLocation(anyString())).thenReturn(visitedLocationTest);
        //WHEN
        userTest.setVisitedLocations(visitedLocationListEmptyTest);
        assertTrue(userTest.getVisitedLocations().isEmpty());
        //THEN
        mockMvcUserGps.perform(MockMvcRequestBuilders.get("/getLocation?userName=jon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(String.valueOf(userTest.getUserId()))))
                .andExpect(jsonPath("$.location.longitude", is(-116.922008)))
                .andExpect(jsonPath("$.location.latitude", is(33.817595)))
                .andDo(print());
    }

    @Test
    public void userGpsGetLocationTest_whenUserNotExist_thenThrowUserNotFoundException() throws Exception {
        //GIVEN
        when(tourGuideClientServiceMock.getUserLocation(anyString())).thenThrow(new UserNotFoundException("user not found"));
        //WHEN
        //THEN
        mockMvcUserGps.perform(MockMvcRequestBuilders.get("/getLocation?userName=unknown"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("user not found",
                        result.getResolvedException().getMessage()))
                .andDo(print());
    }

    @Test
    public void getRewardsTest_whenUsernameIsJon_thenReturnListUserRewardsForJon() throws Exception {
        //GIVEN
        Attraction attraction1 = new Attraction("Disneyland", "Anaheim", "CA", 33.817595D, -117.922008D);
        Attraction attraction2 = new Attraction("Jackson Hole", "Jackson Hole", "WY", 43.582767D, -110.821999D);

        VisitedLocation visitedLocationMock1 = new VisitedLocation(userTest.getUserId(), new Location(33.817595D, -116.922008D), new Date());
        VisitedLocation visitedLocationMock2 = new VisitedLocation(userTest.getUserId(), new Location(34.817595D, -117.922008D), new Date());

        List<UserReward> rewards = Arrays.asList(
                new UserReward(visitedLocationMock1, attraction1, 250),
                new UserReward(visitedLocationMock2, attraction2, 500));
        when(tourGuideClientRewardsServiceMock.getUserRewards(anyString())).thenReturn(rewards);
        //WHEN
        //THEN
        mockMvcUserGps.perform(MockMvcRequestBuilders.get("/getRewards?userName=jon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].visitedLocation.userId", is(String.valueOf(userTest.getUserId()))))
                .andExpect(jsonPath("$.[0].attraction.attractionName", is("Disneyland")))
                .andExpect(jsonPath("$.[0].rewardPoints", is(250)))
                .andDo(print());
    }

    @Test
    public void getNearbyAttractionsTest_whenNoAttractionAreNearOfPosition_thenReturnListNearByAttractionWithFiveAttractions() throws Exception {
        //GIVEN
        List<Attraction> attractions = new ArrayList();
        attractions.add(new Attraction("Disneyland", "Paris", "France", 48.871900D, 2.776623D));
        attractions.add(new Attraction("Belem", "Lisbon", "Portugal", -1.455755D, -48.490180));
        attractions.add(new Attraction("Mojave National Preserve", "Kelso", "CA", 35.141689D, -115.510399D));
        attractions.add(new Attraction("Disneyland", "Anaheim", "CA", 33.817595D, -117.922008D));
        attractions.add(new Attraction("Jackson Hole", "Jackson Hole", "WY", 43.582767D, -110.821999D));
        attractions.add(new Attraction("Mojave National Preserve", "Kelso", "CA", 35.141689D, -115.510399D));

        VisitedLocation visitedLocation = new VisitedLocation(userTest.getUserId(), new Location(33.817595D, -116.922008D), new Date());

        List<NearByAttraction> nearByAttractions = Arrays.asList(
                new NearByAttraction(attractions.get(0).getAttractionName(),
                        new Location(attractions.get(0).getLatitude(), attractions.get(0).getLongitude()),
                        visitedLocation.getLocation(), 500, 200),
                new NearByAttraction(attractions.get(1).getAttractionName(),
                        new Location(attractions.get(1).getLatitude(), attractions.get(0).getLongitude()),
                        visitedLocation.getLocation(), 800, 300),
                new NearByAttraction(attractions.get(2).getAttractionName(),
                        new Location(attractions.get(2).getLatitude(), attractions.get(0).getLongitude()),
                        visitedLocation.getLocation(), 900, 600),
                new NearByAttraction(attractions.get(3).getAttractionName(),
                        new Location(attractions.get(3).getLatitude(), attractions.get(0).getLongitude()),
                        visitedLocation.getLocation(), 1000, 500),
                new NearByAttraction(attractions.get(4).getAttractionName(),
                        new Location(attractions.get(4).getLatitude(), attractions.get(0).getLongitude()),
                        visitedLocation.getLocation(), 1200, 800)
        );
        when(tourGuideClientServiceMock.getUserLocation(anyString())).thenReturn(visitedLocation);
        when(tourGuideClientServiceMock.getNearByAttractions(any(VisitedLocation.class)))
                .thenReturn(nearByAttractions);
        //WHEN
        //THEN
        mockMvcUserGps.perform(MockMvcRequestBuilders.get("/getNearbyAttractions?userName=jon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].attractionName", is("Disneyland")))
                .andExpect(jsonPath("$.[0].distance", is(500)))
                .andExpect(jsonPath("$.[4].distance", is(1200)))
                .andExpect(jsonPath("$.[0].rewardsPoints", is(200)))
                .andExpect(jsonPath("$.length()", is(5)))
                .andDo(print());

    }

    @Test
    public void getNearbyAttractionsTest_whenAllAttractionAreNearOfPosition_thenReturnListNearByAttractionWithFiveAttractions() throws Exception {
        //GIVEN
        List<Attraction> attractions = new ArrayList();
        attractions.add(new Attraction("Disneyland", "Paris", "France", 48.871900D, 2.776623D));
        attractions.add(new Attraction("Belem", "Lisbon", "Portugal", -1.455755D, -48.490180));
        attractions.add(new Attraction("Mojave National Preserve", "Kelso", "CA", 35.141689D, -115.510399D));
        attractions.add(new Attraction("Disneyland", "Anaheim", "CA", 33.817595D, -117.922008D));
        attractions.add(new Attraction("Jackson Hole", "Jackson Hole", "WY", 43.582767D, -110.821999D));
        attractions.add(new Attraction("Mojave National Preserve", "Kelso", "CA", 35.141689D, -115.510399D));

        VisitedLocation visitedLocation = new VisitedLocation(userTest.getUserId(), new Location(33.817595D, -116.922008D), new Date());

        List<NearByAttraction> nearByAttractions = Arrays.asList(
                new NearByAttraction(attractions.get(0).getAttractionName(),
                        new Location(attractions.get(0).getLatitude(), attractions.get(0).getLongitude()),
                        visitedLocation.getLocation(), 50, 200),
                new NearByAttraction(attractions.get(1).getAttractionName(),
                        new Location(attractions.get(1).getLatitude(), attractions.get(0).getLongitude()),
                        visitedLocation.getLocation(), 80, 300),
                new NearByAttraction(attractions.get(2).getAttractionName(),
                        new Location(attractions.get(2).getLatitude(), attractions.get(0).getLongitude()),
                        visitedLocation.getLocation(), 90, 600),
                new NearByAttraction(attractions.get(3).getAttractionName(),
                        new Location(attractions.get(3).getLatitude(), attractions.get(0).getLongitude()),
                        visitedLocation.getLocation(), 100, 500),
                new NearByAttraction(attractions.get(4).getAttractionName(),
                        new Location(attractions.get(4).getLatitude(), attractions.get(0).getLongitude()),
                        visitedLocation.getLocation(), 120, 800)
        );
        when(tourGuideClientServiceMock.getUserLocation(anyString())).thenReturn(visitedLocation);
        when(tourGuideClientServiceMock.getNearByAttractions(any(VisitedLocation.class)))
                .thenReturn(nearByAttractions);
        //WHEN
        //THEN
        mockMvcUserGps.perform(MockMvcRequestBuilders.get("/getNearbyAttractions?userName=jon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].attractionName", is("Disneyland")))
                .andExpect(jsonPath("$.[0].distance", is(50)))
                .andExpect(jsonPath("$.[4].distance", is(120)))
                .andExpect(jsonPath("$.[0].rewardsPoints", is(200)))
                .andExpect(jsonPath("$.length()", is(5)))
                .andDo(print());
    }

    @Test
    public void getNearbyAttractionsTest_whenUserNameNotExist_thenTrowUserNotFoundException() throws Exception {
        //GIVEN
        when(tourGuideClientServiceMock.getUserLocation(anyString())).thenThrow(new UserNotFoundException("user not found"));
        //WHEN
        //THEN
        mockMvcUserGps.perform(MockMvcRequestBuilders.get("/getNearbyAttractions?userName=unknown"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("user not found",
                        result.getResolvedException().getMessage()))
                .andDo(print());
    }
}
