package com.example.aggregation.service;

import com.example.aggregation.client.ShippingClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class ShippingService {

    private final ShippingClient client;
    public volatile Instant shippingEntryTime;
    private ConcurrentLinkedQueue<ShippingRequest> shippingQueue = new ConcurrentLinkedQueue<>();
    private Map<String, List<String>> responses = new HashMap<>();

    public Map<String, List<String>> queryShipping(List<String> shipping) {
        try {
            if (shipping != null && !shipping.isEmpty()) {
                CompletableFuture<Map<String, List<String>>> future = new CompletableFuture<>();
                ShippingRequest request = new ShippingRequest();
                request.future = future;
                request.shippingIds = shipping;
                shippingQueue.add(request);
                if (shippingQueue.size() == 2) {
                    getShippingDetails();
                }
                if (!shippingQueue.isEmpty()) {
                    shippingEntryTime = shippingQueue.element().requestTime;
                }
                return future.get();
            }
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException("ex");
        }
        return Collections.emptyMap();
    }

    public void getShippingDetails() {
        List<ShippingRequest> requests = new ArrayList<>();
        List<String> tracking = new ArrayList<>();
        int size = shippingQueue.size();
        for (int i = 0; i < size; i++) {
            ShippingRequest request = shippingQueue.poll();
            if (request != null) {
                requests.add(request);
                tracking.addAll(request.shippingIds);
            }
        }
        List<String> uniqueShipping = tracking.stream().distinct().toList();
        Mono<Map> shipmentQueue = client.getShipmentQueue(uniqueShipping);
        shipmentQueue.subscribe(s -> responses.putAll(s));
        for (ShippingRequest request : requests) {
            Map<String, List<String>> shippingResponse = new HashMap<>();
            for (String shippingId : request.shippingIds) {
                shippingResponse.put(shippingId, responses.get(shippingId));
            }
            request.future.complete(shippingResponse);
        }
        shippingEntryTime = null;
    }


    static class ShippingRequest {
        List<String> shippingIds;
        CompletableFuture<Map<String, List<String>>> future;
        Instant requestTime = Instant.now();

    }

}






