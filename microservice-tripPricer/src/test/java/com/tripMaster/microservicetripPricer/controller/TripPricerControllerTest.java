package com.tripMaster.microservicetripPricer.controller;

import com.tripMaster.microservicetripPricer.service.TripPricerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tripPricer.Provider;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Class that test the {@link TripPricerController}
 *
 * @author Christine Duarte
 */
@WebMvcTest(TripPricerController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class TripPricerControllerTest {
    @Autowired
    private MockMvc mockMvcTripPricer;

    @MockBean
    private TripPricerServiceImpl tripPricerServiceMock;

    @Test
    public void getProvidersTest_thenReturnListOfProvider() throws Exception {
        //GIVEN
        String apiKey = "apiKey";
        int adults = 2;
        int children = 1;
        int nightsStay = 5;
        int rewardsPoints = 200;
        UUID attractionId = UUID.randomUUID();
        List<Provider> providersTest = Arrays.asList(
                new Provider(UUID.randomUUID(), "Holiday Travels", 250D),
                new Provider(UUID.randomUUID(), "FlyAway Trips", 150D),
                new Provider(UUID.randomUUID(), "Sunny Days", 500D)
        );
        when(tripPricerServiceMock.getPrice(anyString(), any(UUID.class), anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(providersTest);
        //WHEN
        //THEN
        mockMvcTripPricer.perform(MockMvcRequestBuilders.get("/getTripDeals")
                        .param("apiKey", apiKey)
                        .param("attractionId", String.valueOf(attractionId))
                        .param("adults", String.valueOf(adults))
                        .param("children", String.valueOf(children))
                        .param("nightsStay", String.valueOf(nightsStay))
                        .param("rewardsPoints", String.valueOf(rewardsPoints)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name", is("Holiday Travels")))
                .andExpect(jsonPath("$.[0].tripId", is(String.valueOf(providersTest.get(0).tripId))))
                .andExpect(jsonPath("$.[0].price", is(250D)))
                .andDo(print());
    }

}
