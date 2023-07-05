package com.example.aggregation.service;

import com.example.aggregation.model.AggregationDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
@ExtendWith(MockitoExtension.class)
class AggregationServiceImplTest {

    @Mock
    private PricingService pricingService;
    @Mock
    private TrackingService trackingService;
    @Mock
    private ShippingService shippingService;

    @InjectMocks
    AggregationServiceImpl aggregationService;

    @Test
    void getAggregationDetails() throws ExecutionException, InterruptedException {
        //given
        List<String> pricing = new ArrayList<>();
        pricing.add("NL");
        List<String> shippments = new ArrayList<>();
        shippments.add("2094");
        shippments.add("1234");
        List<String> tracking = new ArrayList<>();
        tracking.add("123456");
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
        Mockito.when(pricingService.queryPricing(pricing)).thenReturn(pricingResponse);
        Mockito.when(trackingService.queryTracking(tracking)).thenReturn(trackingResponse);
        Mockito.when(shippingService.queryShipping(shippments)).thenReturn(shippingResponse);
//when
        CompletableFuture<AggregationDetails> result = aggregationService.getAggregationDetails(pricing, tracking, shippments);
        //then
        assertEquals(result.get().getPricing(), pricingResponse);
        assertEquals(result.get().getShipments(), shippingResponse);
        assertEquals(result.get().getTrack(), trackingResponse);
    }
}