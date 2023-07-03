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
public class ShippingService {
    @Autowired
    private Client client;

    class ShippingRequest {
        List<String> shippingIds;
        CompletableFuture<Map<String, ArrayList>> future;
        Instant requestTime = Instant.now();

    }

    public volatile Instant shippingEntryTime;

    ConcurrentLinkedQueue<ShippingRequest> shippingQueue = new ConcurrentLinkedQueue<>();
    Map<String, ArrayList> responses = new HashMap<>();

    public Map<String, ArrayList> queryShipping(List<String> shipping) {
        try {
            if (shipping != null && !shipping.isEmpty()) {
                CompletableFuture<Map<String, ArrayList>> future = new CompletableFuture<>();
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
        return null;
    }

    public void getShippingDetails() {
        List<ShippingRequest> requests = new ArrayList<>();
        List<String> tracking = new ArrayList<>();
        int size = shippingQueue.size();
        for (int i = 0; i < size; i++) {
            ShippingRequest request = shippingQueue.poll();
            requests.add(request);
            tracking.addAll(request.shippingIds);
        }
        List<String> uniqueShipping = tracking.stream().distinct().collect(Collectors.toList());
        Mono<Map> shipmentQueue = client.getShipmentQueue(uniqueShipping);
        shipmentQueue.subscribe(s -> getShippingMap(s));
        for (ShippingRequest request : requests) {
            Map<String, ArrayList> ShippingResponse = new HashMap<>();
            for (String shippingId : request.shippingIds) {
                ShippingResponse.put(shippingId, responses.get(shippingId));
            }
            request.future.complete(ShippingResponse);
        }
        shippingEntryTime = null;
    }

    private Map<String, ArrayList> getShippingMap(Map s) {
        responses.putAll(s);
        return responses;
    }

}






