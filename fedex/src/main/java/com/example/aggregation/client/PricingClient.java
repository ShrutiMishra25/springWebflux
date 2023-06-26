package com.example.aggregation.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@FeignClient(name ="pricing",url = "localhost:8080")
public interface PricingClient {
    @GetMapping("/pricing")
    Map<String,Double > getPricingDetails(@RequestParam String q);
}
