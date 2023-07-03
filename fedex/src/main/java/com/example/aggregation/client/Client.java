package com.example.aggregation.client;

import com.example.aggregation.Exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class Client {
    private static final Logger LOG = Logger.getLogger(Client.class.getName());
    WebClient webClient = WebClient.create("http://localhost:8080");

    public Mono<Map> getPricingQueue(List<String> pricing) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/pricing/").queryParam("q", pricing).build()).retrieve().onStatus(status -> status.value() == HttpStatus.SERVICE_UNAVAILABLE.value(), response -> Mono.error(new ServiceException("Pricing Service Unavailable.", response.statusCode().value()))).bodyToMono(Map.class);
    }

    public Mono<Map> getTrackingQueue(List<String> tracking) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/track/").queryParam("q", tracking).build()).retrieve().onStatus(status -> status.value() == HttpStatus.SERVICE_UNAVAILABLE.value(), response -> Mono.error(new ServiceException("Tracking Service Unavailable.", response.statusCode().value()))).bodyToMono(Map.class);
    }

    public Mono<Map> getShipmentQueue(List<String> shipment) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/shipments/").queryParam("q", shipment).build()).retrieve().onStatus(status -> status.value() == HttpStatus.SERVICE_UNAVAILABLE.value(), response -> Mono.error(new ServiceException("Shipment Service Unavailable.", response.statusCode().value()))).bodyToMono(Map.class);
    }
}
