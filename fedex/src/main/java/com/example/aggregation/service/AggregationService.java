package com.example.aggregation.service;

import com.example.aggregation.model.AggregationDetails;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AggregationService {
    CompletableFuture<AggregationDetails> getAggregationDetails(List<String> pricing, List<String> track, List<String> shipments);
}
