package com.tripMaster.microservicegps.controller;

import com.tripMaster.microservicegps.DAO.InternalUserMapDAO;
import com.tripMaster.microservicegps.exception.UserNotFoundException;
import com.tripMaster.microservicegps.model.User;
import com.tripMaster.microservicegps.service.UserGpsServiceImpl;
import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
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

/**
 * Class that test the {@link UserGpsController}
 *
 * @author Christine Duarte
 */
@WebMvcTest(UserGpsController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class UserGpsControllerTest {

    @Autowired
    private MockMvc mockMvcUserGps;

    @MockBean
    private UserGpsServiceImpl userGpsServiceMock;

    @MockBean
    private GpsUtil gpsUtil;

    @MockBean
    private InternalUserMapDAO internalUserMapDAO;

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
        when(userGpsServiceMock.getUserByUserName(anyString())).thenReturn(Optional.of(userTest));
        when(userGpsServiceMock.getUserLocation(any(User.class))).thenReturn(visitedLocationListTest.get(2));
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
        when(userGpsServiceMock.getUserByUserName(anyString())).thenReturn(Optional.of(userTest));
        when(userGpsServiceMock.getUserLocation(any(User.class))).thenReturn(visitedLocationTest);
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
        when(userGpsServiceMock.getUserByUserName(anyString())).thenThrow(new UserNotFoundException("user not found"));
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


