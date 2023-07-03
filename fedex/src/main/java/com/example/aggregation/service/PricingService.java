package com.example.aggregation.service;

import com.example.aggregation.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class PricingService {
    @Autowired
    private Client client;

    class PricingRequest {
        List<String> pricingIds;
        CompletableFuture<Map<String, Double>> future;
        Instant requestTime = Instant.now();
    }

    ConcurrentLinkedQueue<PricingRequest> pricingQueue = new ConcurrentLinkedQueue<>();
    public volatile Instant entryTime;
    Map<String, Double> responses = new HashMap<>();

    public Map<String, Double> queryPricing(List<String> pricing) {
        try {
            if (pricing != null && !pricing.isEmpty()) {
                CompletableFuture<Map<String, Double>> future = new CompletableFuture<>();
                PricingRequest request = new PricingRequest();
                request.future = future;
                request.pricingIds = pricing;
                pricingQueue.add(request);
                if (pricingQueue.size() == 2) {
                    getPricingDetails();
                }
                if (!pricingQueue.isEmpty()) {
                    entryTime = pricingQueue.element().requestTime;
                }
                return future.get();
            }
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException("ex");
        }
        return null;
    }

    public void getPricingDetails() {
        List<PricingRequest> requests = new ArrayList<>();
        List<String> pricing = new ArrayList<>();
        int size = pricingQueue.size();
        for (int i = 0; i < size; i++) {
            PricingRequest request = pricingQueue.poll();
            requests.add(request);
            pricing.addAll(request.pricingIds);
        }
        List<String> uniquePricing = pricing.stream().distinct().collect(Collectors.toList());
        Mono<Map> pricingQueueResponse = client.getPricingQueue(uniquePricing);
        pricingQueueResponse.subscribe(s -> getPricingMap(s));
        for (PricingRequest request : requests) {
            Map<String, Double> pricingResponse = new HashMap<>();
            for (String pricingId : request.pricingIds) {
                pricingResponse.put(pricingId, responses.get(pricingId));
            }
            request.future.complete(pricingResponse);
        }
        entryTime = null;

    }

    private Map<String, Double> getPricingMap(Map s) {
        responses.putAll(s);
        return responses;
    }
}






