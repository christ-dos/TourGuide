package com.tripMaster.microservicerewards.IT;

import com.tripMaster.microservicerewards.service.RewardsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Class integration test Rewards which verify that all
 * classes works correctly together
 *
 * @author Christine Duarte
 */
@SpringBootTest
@AutoConfigureMockMvc
public class RewardsTestIT {
    @Autowired
    private MockMvc mockMvcRewards;

    @Autowired
    private RewardsServiceImpl rewardsService;

    @Test
    public void getRewardsPointsTest_thenReturnAnIntegerWithRewardsPoints() throws Exception {
        //GIVEN
        UUID attractionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        //WHEN
        int rewardsPoint = rewardsService.getRewardPoints(attractionId, userId);
        //THEN
        mockMvcRewards.perform(MockMvcRequestBuilders.get("/getRewards")
                        .param("attractionId", String.valueOf(attractionId))
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(Integer.class)))
                .andDo(print());

        assertNotNull(rewardsPoint);
    }

}
