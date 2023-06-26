package com.example.aggregation.service;

import com.example.aggregation.model.AggregationDetails;
import reactor.core.publisher.Flux;

import java.util.List;

public interface AggregationService {

    public Flux<AggregationDetails> getAllAggregation(List<String> pricing, List<String> track, List<String> shipments);
}
