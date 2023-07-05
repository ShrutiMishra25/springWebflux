package com.example.aggregation.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
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
class PricingClientTest {
    PricingClient pricingClient;
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
        pricingClient = new PricingClient(webClientMock);
    }

    @Test
    void getPricingQueue() {
        Map<String, Double> pricingResponse = new HashMap<>();
        pricingResponse.put("CN", 82.17785040193824);
        pricingResponse.put("NL", 78.17785040193824);

        when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock.uri(Mockito.any(Function.class))).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Map.class)).thenReturn(Mono.just(pricingResponse));
        List<String> pricing = new ArrayList<>();
        pricing.add("NL");
        Mono<Map> pricingMap = pricingClient.getPricingQueue(pricing);

        StepVerifier.create(pricingMap).expectNextMatches(price -> price.get("NL").equals(78.17785040193824)).verifyComplete();
    }

}

