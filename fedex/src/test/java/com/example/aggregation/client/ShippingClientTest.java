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
class ShippingClientTest {

    ShippingClient shippingClient;
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
        shippingClient = new ShippingClient(webClientMock);
    }

    @Test
    void getShippingQueue() {
        Map<String, ArrayList> shippingResponse = new HashMap<>();
        ArrayList<String> shippingList1 = new ArrayList<>();
        shippingList1.add("box");
        shippingList1.add("pellet");
        shippingList1.add("envelope");
        ArrayList<String> shippingList2 = new ArrayList<>();
        shippingList2.add("box");
        shippingList2.add("pellet");
        shippingList2.add("envelope");
        shippingResponse.put("2094", shippingList1);
        shippingResponse.put("1234", shippingList2);
        List<String> shippments = new ArrayList<>();
        shippments.add("2094");
        shippments.add("1234");
        when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock.uri(Mockito.any(Function.class))).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Map.class)).thenReturn(Mono.just(shippingResponse));
        Mono<Map> shippingMap = shippingClient.getShipmentQueue(shippments);
        StepVerifier.create(shippingMap)
                .recordWith(ArrayList::new)
                .thenConsumeWhile(x -> true)
                .expectRecordedMatches(elements -> !elements.isEmpty())
                .verifyComplete();
        StepVerifier.create(shippingMap).expectNextMatches(ship -> ship.get("1234").equals(shippingList1)).verifyComplete();
        StepVerifier.create(shippingMap).expectNextMatches(ship -> ship.get("2094").equals(shippingList2)).verifyComplete();

    }
}