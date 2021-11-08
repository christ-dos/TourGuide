package com.tripMaster.microservicerewards.service;

import com.tripMaster.microservicerewards.proxies.MicroserviceGpsProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rewardCentral.RewardCentral;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Class that test the {@link RewardsServiceImpl}
 *
 * @author Christine Duarte
 */
@ExtendWith(MockitoExtension.class)
public class RewardsServiceTest {

    private RewardsServiceImpl rewardsServiceImplTest;

    @Mock
    private RewardCentral rewardsCentralMock;

    @Mock
    private MicroserviceGpsProxy microserviceGpsProxyMock;

    @BeforeEach
    public void setUpPerTest() {
        rewardsServiceImplTest = new RewardsServiceImpl(rewardsCentralMock, microserviceGpsProxyMock);
    }

    @Test
    public void getRewardPointTest_thenReturn200RewardPoints() {
        //GIVEN
        UUID attractionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(rewardsCentralMock.getAttractionRewardPoints(any(UUID.class), any(UUID.class))).thenReturn(200);
        //WHEN
        int rewardsPointResult = rewardsServiceImplTest.getRewardPoints(attractionId, userId);
        //THEN
        assertEquals(200, rewardsPointResult);
    }
}
