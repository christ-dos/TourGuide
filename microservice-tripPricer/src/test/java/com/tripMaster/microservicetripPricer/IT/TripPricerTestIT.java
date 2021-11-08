package com.tripMaster.microservicetripPricer.IT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

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
}
