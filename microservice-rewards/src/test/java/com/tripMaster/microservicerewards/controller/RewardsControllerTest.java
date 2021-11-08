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
import static org.mockito.ArgumentMatchers.any;
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


    @Test
    public void getRewardsPointsTest_thenReturnAnIntegerWithRewardsPoints() throws Exception {
        //GIVEN
        UUID attractionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(rewardsServiceImplMock.getRewardPoints(any(UUID.class), any(UUID.class))).thenReturn(220);
        //WHEN
        //THEN
        mockMvcRewards.perform(MockMvcRequestBuilders.get("/getRewards")
                        .param("attractionId" , String.valueOf(attractionId))
                        .param("userId" , String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(220)))
                .andDo(print());
    }

}
