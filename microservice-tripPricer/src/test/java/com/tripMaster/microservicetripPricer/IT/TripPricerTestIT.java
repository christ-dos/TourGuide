package com.tripMaster.microservicetripPricer.IT;

import com.tripMaster.microservicetripPricer.service.TripPricerServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tripPricer.Provider;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Class integration test TripPricer which verify that all
 * classes works correctly together
 *
 * @author Christine Duarte
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TripPricerTestIT {
    @Autowired
    private MockMvc mockMvcTripPricer;

    @Autowired
    private TripPricerServiceImpl tripPricerService;

    @Test
    public void getProvidersTest_thenReturnListOfProvider() throws Exception {
        //GIVEN
        String apiKey = "apiKey";
        int adults = 2;
        int children = 1;
        int nightsStay = 5;
        int rewardsPoints = 200;
        UUID attractionId = UUID.randomUUID();
        //WHEN
        List<Provider> providers = tripPricerService.getPrice(apiKey, attractionId, adults, children, nightsStay, rewardsPoints);
        //THEN
        mockMvcTripPricer.perform(MockMvcRequestBuilders.get("/getTripDeals")
                        .param("apiKey", apiKey)
                        .param("attractionId", String.valueOf(attractionId))
                        .param("adults", String.valueOf(adults))
                        .param("children", String.valueOf(children))
                        .param("nightsStay", String.valueOf(nightsStay))
                        .param("rewardsPoints", String.valueOf(rewardsPoints)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name", is(notNullValue(String.class))))
                .andExpect(jsonPath("$.[0].tripId", is(notNullValue(UUID.class))))
                .andExpect(jsonPath("$.[0].price", is(notNullValue(Double.class))))
                .andDo(print());

        assertTrue(providers.size() > 0);
        assertNotNull(providers.get(0).tripId);

    }
}
