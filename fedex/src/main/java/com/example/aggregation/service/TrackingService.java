package com.example.aggregation.service;

import com.example.aggregation.client.TrackingClient;
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
public class TrackingService {

    private final TrackingClient client;
    public volatile Instant trackingEntryTime;
    private ConcurrentLinkedQueue<TrackingRequest> trackingQueue = new ConcurrentLinkedQueue<>();
    private Map<String, String> responses = new HashMap<>();

    public Map<String, String> queryTracking(List<String> tracking) {
        try {
            if (tracking != null && !tracking.isEmpty()) {
                CompletableFuture<Map<String, String>> future = new CompletableFuture<>();
                TrackingRequest request = new TrackingRequest();
                request.future = future;
                request.trackingIds = tracking;
                trackingQueue.add(request);
                if (trackingQueue.size() == 2) {
                    getTrackingDetails();
                }
                if (!trackingQueue.isEmpty()) {
                    trackingEntryTime = trackingQueue.element().requestTime;
                }
                return future.get();
            }
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException("ex");
        }
        return Collections.emptyMap();
    }

    public void getTrackingDetails() {
        List<TrackingRequest> requests = new ArrayList<>();
        List<String> tracking = new ArrayList<>();
        int size = trackingQueue.size();
        for (int i = 0; i < size; i++) {
            TrackingRequest request = trackingQueue.poll();
            if (request != null) {
                requests.add(request);
                tracking.addAll(request.trackingIds);
            }
        }
        List<String> uniqueTracking = tracking.stream().distinct().toList();
        Mono<Map> shippingResponses = client.getTrackingQueue(uniqueTracking);
        shippingResponses.subscribe(s -> responses.putAll(s));
        for (TrackingRequest request : requests) {
            Map<String, String> trackingResponse = new HashMap<>();
            for (String trackingId : request.trackingIds) {
                trackingResponse.put(trackingId, responses.get(trackingId));
            }
            request.future.complete(trackingResponse);
        }
        trackingEntryTime = null;
    }

    static class TrackingRequest {
        private List<String> trackingIds;
        private CompletableFuture<Map<String, String>> future;
        private Instant requestTime = Instant.now();

    }
}






