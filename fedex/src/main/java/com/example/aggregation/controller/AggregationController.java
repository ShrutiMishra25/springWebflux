
package com.example.aggregation.controller;

import com.example.aggregation.model.AggregationDetails;
import com.example.aggregation.service.AggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
public class AggregationController {
    private final AggregationService aggregationService;

    @GetMapping("/aggregationqueue")
    public ResponseEntity<CompletableFuture<AggregationDetails>> getAggregationDetailsWithQueue(@RequestParam(required = false) List<String> pricing, @RequestParam(required = false) List<String> track, @RequestParam(required = false) List<String> shipments) throws ExecutionException, InterruptedException {
        var aggregationDetails = aggregationService.getAggregationDetails(pricing, track, shipments);
        return ResponseEntity.status(HttpStatus.OK).body(aggregationDetails);
    }
}

