package com.example.aggregation.service;

import com.example.aggregation.client.PricingClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
@ExtendWith(MockitoExtension.class)
class PricingServiceTest {
    @Mock
    PricingClient client;
    @Mock
    CompletableFuture<Map<String, Double>> future;
    @InjectMocks
    PricingService pricingService;

    @Test
    void queryPricing() throws ExecutionException, InterruptedException {
        List<String> pricing = new ArrayList<>();
        pricing.add("NL");
        Map<String, Double> pricingResponse = new HashMap<>();
        pricingResponse.put("CN", 82.17785040193824);
        pricingResponse.put("NL", 78.17785040193824);
        Mockito.when(future.get()).thenReturn(pricingResponse);
       // Mockito.when(client.getPricingQueue(pricing)).thenReturn(Mono.just(pricingResponse));
          Map<String, Double> result = pricingService.queryPricing(pricing);
        Map<String, Double> result2 = pricingService.queryPricing(pricing);

        assertEquals(result.get("NL"), 78.17785040193824);
        assertEquals(result.size(),1);

    }
}