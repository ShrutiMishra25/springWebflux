
package com.example.aggregation.controller;

import com.example.aggregation.client.PricingService;
import com.example.aggregation.model.AggregationDetails;
import com.example.aggregation.service.AggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
public class AggregationController {
private final AggregationService aggregationService;
    private final PricingService pricingService;




    @GetMapping("/aggregation")
        public ResponseEntity<Flux<AggregationDetails>> getAggregationDetails(@RequestParam(required = false) List<String> pricing, @RequestParam(required = false) List<String> track, @RequestParam(required = false) List<String> shipments) {
           var aggregationDetails = aggregationService.getAllAggregation(pricing,track,shipments);
            return ResponseEntity.status(HttpStatus.OK).body(aggregationDetails);
        }
    @GetMapping("/aggregationqueue")
    public ResponseEntity<Map<String, Double>> getAggregationDetailsWithQueue(@RequestParam(required = false)  String pricing, @RequestParam(required = false) List<String> track, @RequestParam(required = false) List<String> shipments) throws ExecutionException, InterruptedException {
        var aggregationDetails = pricingService.queryOrderBatch(pricing);
        return ResponseEntity.status(HttpStatus.OK).body(aggregationDetails);
    }
}

