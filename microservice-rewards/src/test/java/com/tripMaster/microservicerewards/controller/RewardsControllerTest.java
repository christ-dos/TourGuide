package com.tripMaster.microservicerewards.controller;

import com.tripMaster.microservicerewards.exception.UserNotFoundException;
import com.tripMaster.microservicerewards.model.Attraction;
import com.tripMaster.microservicerewards.model.Location;
import com.tripMaster.microservicerewards.model.User;
import com.tripMaster.microservicerewards.model.VisitedLocation;
import com.tripMaster.microservicerewards.proxies.MicroserviceGpsProxy;
import com.tripMaster.microservicerewards.service.RewardsServiceImpl;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Class that test the {@link RewardsController}
 *
 * @author Christine Duarte
 */
@WebMvcTest(RewardsController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class RewardsControllerTest {

    @Autowired
    private MockMvc mockMvcRewards;

    @MockBean
    private RewardsServiceImpl rewardsServiceImplMock;

    @MockBean
    private MicroserviceGpsProxy microserviceGpsProxyMock;

    @BeforeEach
    public void setupPerTest() {

    }

//    @Test
//    public void getAttractionsTest_whenListContainedThreeElements_thenReturnListWithThreeAttractions() throws Exception {
//        //GIVEN
//        List<Attraction> attractions = new ArrayList();
//        attractions.add(new Attraction("Disneyland", "Anaheim", "CA", 33.817595D, -117.922008D));
//        attractions.add(new Attraction("Jackson Hole", "Jackson Hole", "WY", 43.582767D, -110.821999D));
//        attractions.add(new Attraction("Mojave National Preserve", "Kelso", "CA", 35.141689D, -115.510399D));
//        when(microserviceGpsProxyMock.getAttractions()).thenReturn(attractions);
//        //WHEN
//        //THEN
//        mockMvcRewards.perform(MockMvcRequestBuilders.get("/getAttractions"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.[0].attractionName", is("Disneyland")))
//                .andExpect(jsonPath("$.[0].latitude", is(33.817595)))
//                .andExpect(jsonPath("$.[0].longitude", is(-117.922008)))
//                .andDo(print());
//
//    }
//
//    @Test
//    public void calculateRewardsTest_whenUsernameExist() throws Exception {
//        //GIVEN
//        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
//        //WHEN
//        when(microserviceGpsProxyMock.getUser(anyString())).thenReturn(user);
//        doNothing().when(rewardsServiceImplMock).calculateRewards(user);
//        //THEN
//        mockMvcRewards.perform(MockMvcRequestBuilders.get("/getRewards?userName=jon"))
//                .andExpect(status().isOk())
//                .andDo(print());
//
//    }

    @Test
    public void userGpsGetLocationTest_whenUsernameExist_thenReturnVisitedLocation() throws Exception {
        //GIVEN
        User userTest = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocationTest = new VisitedLocation(userTest.getUserId(), new Location(33.817595D, -116.922008D), new Date());
        when(microserviceGpsProxyMock.userGpsGetLocation(anyString())).thenReturn(visitedLocationTest);
        //WHEN
        //THEN
        mockMvcRewards.perform(MockMvcRequestBuilders.get("/getLocation?userName=jon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(String.valueOf(userTest.getUserId()))))
                .andExpect(jsonPath("$.location.longitude", is(-116.922008)))
                .andExpect(jsonPath("$.location.latitude", is(33.817595)))
                .andDo(print());
    }

    @Test
    public void userGpsGetLocationTest_whenUserNotExist_thenTrowUserNotFoundException() throws Exception {
        //GIVEN
        when(microserviceGpsProxyMock.userGpsGetLocation(anyString())).thenThrow(new UserNotFoundException("user not found"));
        //WHEN
        //THEN
        mockMvcRewards.perform(MockMvcRequestBuilders.get("/getLocation?userName=unknown"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("user not found",
                        result.getResolvedException().getMessage()))
                .andDo(print());
    }

    @Test
    public void getUserTest_whenUserExist_thenReturnUserFound() throws Exception {
        //GIVEN
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        //WHEN
        when(microserviceGpsProxyMock.getUser(anyString())).thenReturn(user);
        //WHEN
        //THEN
        mockMvcRewards.perform(MockMvcRequestBuilders.get("/getUser?userName=jon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(String.valueOf(user.getUserId()))))
                .andExpect(jsonPath("$.userName", is("jon")))
                .andExpect(jsonPath("$.emailAddress", is("jon@tourGuide.com")))
                .andDo(print());
    }

    @Test
    public void getUserTest_whenUserNotExist_thenThrowUserFoundException() throws Exception {
        //GIVEN
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        //WHEN
        when(microserviceGpsProxyMock.getUser(anyString())).thenThrow(new UserNotFoundException("User not found"));
        //WHEN
        //THEN
        mockMvcRewards.perform(MockMvcRequestBuilders.get("/getUser?userName=unknown"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("User not found",
                        result.getResolvedException().getMessage()))
                .andDo(print());
    }
}
