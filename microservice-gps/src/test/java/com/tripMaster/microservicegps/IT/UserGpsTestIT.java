package com.tripMaster.microservicegps.IT;

import com.tripMaster.microservicegps.service.UserGpsServiceImpl;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Class integration test UserGps which verify that all
 * classes works correctly together
 *
 * @author Christine Duarte
 */
@SpringBootTest
@AutoConfigureMockMvc
public class UserGpsTestIT {
    @Autowired
    private MockMvc mockMvcUserGps;

    @Autowired
    private UserGpsServiceImpl userGpsServiceTest;


    @Test
    public  void trackUserLocationTest_thenReturnVisitedLocationForUser() throws Exception {
        //GIVEN
        UUID userId = UUID.randomUUID();
        //WHEN
        VisitedLocation visitedLocationResult = userGpsServiceTest.trackUserLocation(userId);
        //THEN
        mockMvcUserGps.perform(MockMvcRequestBuilders.get("/getLocation?getLocation")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(String.valueOf(userId))))
                .andExpect(jsonPath("$.location.longitude",is(notNullValue())))
                .andExpect(jsonPath("$.location.latitude",is(notNullValue())))
                .andDo(print());

        assertNotNull(visitedLocationResult);
        assertTrue(visitedLocationResult.userId ==  userId);
    }

    @Test
    public  void getAttractionsTest_whenListContainedThreeElements_thenReturnListWithThreeAttractions() throws Exception {
        //GIVEN
        //WHEN
        List<Attraction> attractionList= userGpsServiceTest.getAttractions();
        //THEN
        mockMvcUserGps.perform(MockMvcRequestBuilders.get("/getAttractions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].attractionName", is("Disneyland")))
                .andExpect(jsonPath("$.[0].latitude", is(33.817595)))
                .andExpect(jsonPath("$.[0].longitude", is(-117.922008)))
                .andDo(print());

        assertTrue(attractionList.size() > 0);
        assertTrue(attractionList.get(0).attractionName.contains("Disneyland"));
        assertEquals(-117.922008,attractionList.get(0).longitude );
        assertEquals(33.817595,attractionList.get(0).latitude);
    }

}
