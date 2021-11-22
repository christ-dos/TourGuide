package com.tripMaster.microservicerewards.controller;

import com.tripMaster.microservicerewards.service.RewardsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
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


    @Test
    public void getRewardsPointsTest_thenReturnAnIntegerWithRewardsPoints() throws Exception {
        //GIVEN
        UUID attractionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(rewardsServiceImplMock.getRewardPoints(any(UUID.class), any(UUID.class))).thenReturn(220);
        //WHEN
        //THEN
        mockMvcRewards.perform(MockMvcRequestBuilders.get("/getRewards")
                        .param("attractionId", String.valueOf(attractionId))
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(220)))
                .andDo(print());
    }

}
