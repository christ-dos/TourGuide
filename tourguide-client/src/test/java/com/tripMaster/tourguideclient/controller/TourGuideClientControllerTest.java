package com.tripMaster.tourguideclient.controller;

import com.tripMaster.tourguideclient.exception.UserNotFoundException;
import com.tripMaster.tourguideclient.model.*;
import com.tripMaster.tourguideclient.service.TourGuideClientRewardsService;
import com.tripMaster.tourguideclient.service.TourGuideClientServiceImpl;
import com.tripMaster.tourguideclient.utils.Tracker;
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    private TourGuideClientServiceImpl tourGuideClientServiceTest;

    @MockBean
    private TourGuideClientRewardsService tourGuideClientRewardsService;


    private User userTest;

    @BeforeEach
    public void setupPerTest() {
        userTest = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    }


    @Test
    public void userGpsGetLocationTest_whenUserNameIsJonAndVisitedLocationsListIsNotEmpty_thenReturnVisitedLocationOfJon() throws Exception {
        //GIVEN
        List<VisitedLocation> visitedLocationListTest = Arrays.asList(
                new VisitedLocation(userTest.getUserId(), new Location(33.817595D, -116.922008D), new Date()),
                new VisitedLocation(userTest.getUserId(), new Location(34.817595D, -117.922008D), new Date()),
                new VisitedLocation(userTest.getUserId(), new Location(35.817595D, -118.922008D), new Date())
        );
        when(tourGuideClientServiceTest.getUserLocation(anyString())).thenReturn(visitedLocationListTest.get(2));
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
        List<VisitedLocation> visitedLocationListEmptyTest = new ArrayList<>();
        when(tourGuideClientServiceTest.getUserLocation(anyString())).thenReturn(visitedLocationTest);
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
        when(tourGuideClientServiceTest.getUserLocation(anyString())).thenThrow(new UserNotFoundException("user not found"));
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
        when(tourGuideClientRewardsService.getUserRewards(anyString())).thenReturn(rewards);
        //WHEN
        //THEN
        mockMvcUserGps.perform(MockMvcRequestBuilders.get("/getRewards?userName=jon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].visitedLocation.userId", is(String.valueOf(userTest.getUserId()))))
                .andExpect(jsonPath("$.[0].attraction.attractionName", is("Disneyland")))
                .andExpect(jsonPath("$.[0].rewardPoints", is(250)))
                .andDo(print());
    }
}
