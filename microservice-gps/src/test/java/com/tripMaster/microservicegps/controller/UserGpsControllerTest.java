package com.tripMaster.microservicegps.controller;

import com.tripMaster.microservicegps.service.UserGpsServiceImpl;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
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

    @Test
    public  void trackUserLocationTest() throws Exception {
        //GIVEN
        UUID userId = UUID.randomUUID();
        VisitedLocation visitedLocationMock = new VisitedLocation(userId, new Location(33.817595D, -116.922008D), new Date());
        when(userGpsServiceMock.trackUserLocation(userId)).thenReturn(visitedLocationMock);
        //WHEN
        //THEN
        mockMvcUserGps.perform(MockMvcRequestBuilders.get("/getLocation?getLocation")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(String.valueOf(userId))))
                .andExpect(jsonPath("$.location.longitude", is(-116.922008)))
                .andExpect(jsonPath("$.location.latitude", is(33.817595D)))
                .andDo(print());
    }

    @Test
    public  void getAttractionsTest_whenListContainedThreeElements_thenReturnListWithThreeAttractions() throws Exception {
        //GIVEN
        List<Attraction> attractions = new ArrayList();
        attractions.add(new Attraction("Disneyland", "Anaheim", "CA", 33.817595D, -117.922008D));
        attractions.add(new Attraction("Jackson Hole", "Jackson Hole", "WY", 43.582767D, -110.821999D));
        attractions.add(new Attraction("Mojave National Preserve", "Kelso", "CA", 35.141689D, -115.510399D));
        when(userGpsServiceMock.getAttractions()).thenReturn(attractions);
        //WHEN
        //THEN
        mockMvcUserGps.perform(MockMvcRequestBuilders.get("/getAttractions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].attractionName", is("Disneyland")))
                .andExpect(jsonPath("$.[0].latitude", is(33.817595)))
                .andExpect(jsonPath("$.[0].longitude", is(-117.922008)))
                .andDo(print());
    }
}


