package com.tripMaster.tourguideclient.controller;

import com.tripMaster.tourguideclient.exception.UserNotFoundException;
import com.tripMaster.tourguideclient.model.Location;
import com.tripMaster.tourguideclient.model.UserTourGuideClient;
import com.tripMaster.tourguideclient.model.VisitedLocation;
import com.tripMaster.tourguideclient.proxies.MicroserviceUserGpsProxy;
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
    private MicroserviceUserGpsProxy microserviceUserGpsProxyMock;

    private UserTourGuideClient userTest;

    @BeforeEach
    public void setupPerTest() {
        userTest = new UserTourGuideClient(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    }

    @Test
    public void userGpsGetLocationTest_whenUserNameIsJonAndVisitedLocationsListIsNotEmpty_thenReturnVisitedLocationOfJon() throws Exception {
        //GIVEN
        List<VisitedLocation> visitedLocationListTest = Arrays.asList(
                new VisitedLocation(userTest.getUserId(), new Location(33.817595D, -116.922008D), new Date()),
                new VisitedLocation(userTest.getUserId(), new Location(34.817595D, -117.922008D), new Date()),
                new VisitedLocation(userTest.getUserId(), new Location(35.817595D, -118.922008D), new Date())
        );
        when(microserviceUserGpsProxyMock.userGpsGetLocation(anyString())).thenReturn(visitedLocationListTest.get(2));
        //WHEN
        userTest.setVisitedLocations(visitedLocationListTest);
        System.out.println(userTest.toString());
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
        when(microserviceUserGpsProxyMock.userGpsGetLocation(anyString())).thenReturn(visitedLocationTest);
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
    public void userGpsGetLocationTest_whenUserNotExist_thenTrowUserNotFoundException() throws Exception {
        //GIVEN
        when(microserviceUserGpsProxyMock.userGpsGetLocation(anyString())).thenThrow(new UserNotFoundException("user not found"));
        //WHEN
        //THEN
        mockMvcUserGps.perform(MockMvcRequestBuilders.get("/getLocation?userName=unknown"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("user not found",
                        result.getResolvedException().getMessage()))
                .andDo(print());
    }

}
