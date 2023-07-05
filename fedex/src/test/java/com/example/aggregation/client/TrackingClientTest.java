package com.example.aggregation.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrackingClientTest {
    TrackingClient trackingClient;
    @Mock
    private WebClient webClientMock;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersMock;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriMock;
    @Mock
    private WebClient.ResponseSpec responseMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @BeforeEach
    void setUp() {
        trackingClient = new TrackingClient(webClientMock);
    }

    @Test
    void getTrackingQueue() {
        Map<String, String> trackingResponse = new HashMap<>();
        trackingResponse.put("123456", "Delivered");
        trackingResponse.put("6756", "  Sent");

        when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock.uri(Mockito.any(Function.class))).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Map.class)).thenReturn(Mono.just(trackingResponse));
        List<String> tracking = new ArrayList<>();
        tracking.add("123456");
        Mono<Map> trackingMap = trackingClient.getTrackingQueue(tracking);

        StepVerifier.create(trackingMap).expectNextMatches(track -> track.get("123456").equals("Delivered")).verifyComplete();
    }
}
