package com.example.aggregation.controller;

import com.example.aggregation.model.AggregationDetails;
import com.example.aggregation.service.AggregationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AggregationControllerTest {
    @InjectMocks
    AggregationController aggregationController;
    @Mock
    AggregationService aggregationService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(aggregationController).build();
    }

    @Test
    void getAggregationDetailsWithQueue() throws ExecutionException, InterruptedException {
        //given
        List<String> pricing = new ArrayList<>();
        pricing.add("NL");
        List<String> shippments = new ArrayList<>();
        shippments.add("2094");
        shippments.add("1234");
        List<String> tracking = new ArrayList<>();
        tracking.add("123456");
        AggregationDetails aggregationDetails = new AggregationDetails();
        Map<String, String> trackingResponse = new HashMap<>();
        trackingResponse.put("123456", "Delivered");
        trackingResponse.put("6756", "  Sent");
        Map<String, Double> pricingResponse = new HashMap<>();
        pricingResponse.put("CN", 82.17785040193824);
        pricingResponse.put("NL", 78.17785040193824);
        Map<String, List<String>> shippingResponse = new HashMap<>();
        List<String> shippingList1 = new ArrayList<>();
        shippingList1.add("box");
        shippingList1.add("pellet");
        shippingList1.add("envelope");
        shippingResponse.put("2094", shippingList1);
        aggregationDetails.setTrack(trackingResponse);
        aggregationDetails.setPricing(pricingResponse);
        aggregationDetails.setShipments(shippingResponse);
        CompletableFuture<AggregationDetails> future = CompletableFuture.completedFuture(aggregationDetails);
        Mockito.when(aggregationService.getAggregationDetails(pricing, shippingList1, tracking)).thenReturn(future);
        //when
        ResponseEntity<CompletableFuture<AggregationDetails>> result = aggregationController.getAggregationDetailsWithQueue(pricing, shippingList1, tracking);
        //then
        assertThat(result.getBody().equals(future));
    }

    @Test
    void getAggregationDetailsWithQueue2() throws Exception {
        // when
        var mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/aggregationqueue").param("pricing", "NL").param("track", "2049").param("shipping", "Block")).andReturn();
        // then
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
    }
}