package com.tripMaster.microservicetripPricer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Class that test the {@link TripPricerServiceImpl}
 *
 * @author Christine Duarte
 */
@ExtendWith(MockitoExtension.class)
public class TripPricerServiceImplTest {

    private TripPricerServiceImpl tripPricerService;

    @Mock
    private TripPricer tripPricerMock;

    @BeforeEach
    public void setUpPerTest() {
        tripPricerService = new TripPricerServiceImpl(tripPricerMock);
    }

    @Test
    public void getPriceTest_thenReturnListOfProvider() {
        //GIVEN
        String apiKey = "apiKey";
        int adults = 2;
        int children = 1;
        int nightsStay = 5;
        int rewardsPoints = 200;
        UUID attractionId = UUID.randomUUID();
        List<Provider> providersTest = Arrays.asList(
                new Provider(UUID.randomUUID(), "Holiday Travels", 200D),
                new Provider(UUID.randomUUID(), "FlyAway Trips", 150D),
                new Provider(UUID.randomUUID(), "Sunny Days", 500D)
        );
        when(tripPricerMock.getPrice(anyString(), any(UUID.class), anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(providersTest);
        //WHEN
        List<Provider> providersResult = tripPricerService.getPrice(apiKey, attractionId, adults, children, nightsStay, rewardsPoints);
        //THEN
        assertEquals(3, providersResult.size());
        assertEquals(200, providersResult.get(0).price);
        assertEquals("FlyAway Trips", providersResult.get(1).name);
    }
}
