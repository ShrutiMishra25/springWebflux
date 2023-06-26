package com.example.aggregation.service;

import com.example.aggregation.client.Client;
import com.example.aggregation.model.AggregationDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AggregationServiceImpl implements AggregationService{
    private final Client client;
    @Override
    public Flux<AggregationDetails> getAllAggregation(List<String> pricing, List<String> track, List<String> shipments) {
return client.fetchAggregationDetails(pricing,track,shipments);
    }
}
