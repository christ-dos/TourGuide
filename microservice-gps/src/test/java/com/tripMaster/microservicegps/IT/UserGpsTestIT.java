package com.tripMaster.microservicegps.IT;

import com.tripMaster.microservicegps.DAO.InternalUserMapDAO;
import com.tripMaster.microservicegps.exception.UserNotFoundException;
import com.tripMaster.microservicegps.model.User;
import com.tripMaster.microservicegps.service.UserGpsServiceImpl;
import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Class integration test for {@link User} which verify that all
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
    private InternalUserMapDAO internalUserMapDAO;

    @Autowired
    private UserGpsServiceImpl userGpsService;

    private User userTest;

    @BeforeEach
    public void setupPerTest() {
        userTest = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

    }

    @Test
    public void userGpsGetLocationTest_whenUserNameIsJonAndVisitedLocationsListIsNotEmpty_thenReturnLastVisitedLocationOfJon() throws Exception {
        //GIVEN
        List<VisitedLocation> visitedLocationListTest = Arrays.asList(
                new VisitedLocation(userTest.getUserId(), new Location(33.817595D, -116.922008D), new Date()),
                new VisitedLocation(userTest.getUserId(), new Location(34.817595D, -117.922008D), new Date()),
                new VisitedLocation(userTest.getUserId(), new Location(35.817595D, -118.922008D), new Date())
        );
        //WHEN
        userTest.setVisitedLocations(visitedLocationListTest);
        internalUserMapDAO.addUser(userTest);
        //THEN
        mockMvcUserGps.perform(MockMvcRequestBuilders.get("/getLocation?userName=jon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(String.valueOf(userTest.getUserId()))))
                .andExpect(jsonPath("$.location.longitude", is(-118.922008D)))
                .andExpect(jsonPath("$.location.latitude", is(35.817595D)))
                .andDo(print());

        User user = internalUserMapDAO.getUser("jon");
        assertTrue(user.getVisitedLocations().size() == 3);
        assertEquals(-118.922008D, user.getVisitedLocations().get(2).location.longitude);
        assertEquals(35.817595D, user.getVisitedLocations().get(2).location.latitude);

        VisitedLocation visitedLocation = userGpsService.getUserLocation(user);
        assertEquals(user.getVisitedLocations().get(2), visitedLocation);
    }

    @Test
    public void userGpsGetLocationTest_whenUserNameIsJonaAndVisitedLocationsListEmpty_thenReturnVisitedLocationOfJonaTracked() throws Exception {
        //GIVEN
        User userTest1 = new User(UUID.randomUUID(), "jona", "000", "jon@tourGuide.com");

        List<VisitedLocation> visitedLocationListEmpty = new ArrayList<>();
        //WHEN
        userTest1.setVisitedLocations(visitedLocationListEmpty);
        internalUserMapDAO.addUser(userTest1);
        assertTrue(userTest1.getVisitedLocations().size() == 0);
        VisitedLocation visitedLocationTracked = userGpsService.trackUserLocation(userTest1);
        assertEquals(1,userTest1.getVisitedLocations().size() );
        //THEN
        mockMvcUserGps.perform(MockMvcRequestBuilders.get("/getLocation?userName=jona"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(String.valueOf(userTest1.getUserId()))))
                .andExpect(jsonPath("$.location.longitude", is(visitedLocationTracked.location.longitude)))
                .andExpect(jsonPath("$.location.latitude", is(visitedLocationTracked.location.latitude)))
                .andDo(print());

        User user = internalUserMapDAO.getUser("jona");
        assertTrue(user.getVisitedLocations().size() == 1);
        assertEquals(visitedLocationTracked.location.longitude, user.getVisitedLocations().get(0).location.longitude);
        assertEquals(visitedLocationTracked.location.latitude, user.getVisitedLocations().get(0).location.latitude);

        VisitedLocation visitedLocation = userGpsService.getUserLocation(user);
        assertEquals(user.getVisitedLocations().get(0), visitedLocation);
    }

    @Test
    public void userGpsGetLocationTest_whenUserNotExist_thenTrowUserNotFoundException() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcUserGps.perform(MockMvcRequestBuilders.get("/getLocation?userName=unknown"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("user not found",
                        result.getResolvedException().getMessage()))
                .andDo(print());
        User user = internalUserMapDAO.getUser("Unknown");
        assertNull(user);
        assertThrows(UserNotFoundException.class, () -> userGpsService.getUserByUserName("Unknown"));
    }

}
