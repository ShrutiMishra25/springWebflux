package com.example.aggregation.client;

import com.example.aggregation.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static com.example.aggregation.client.WebClientConfig.SHIPPING_WEB_CLIENT;

@Component
@RequiredArgsConstructor
public class ShippingClient {

    @Qualifier(SHIPPING_WEB_CLIENT)
    private final WebClient webClient;

    public Mono<Map> getShipmentQueue(List<String> shipment) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/shipments/")
                        .queryParam("q", shipment)
                        .build())
                .retrieve()
                .onStatus(status -> status.value() == HttpStatus.SERVICE_UNAVAILABLE.value(), response -> Mono.error(new ServiceException("Shipment Service Unavailable.", response.statusCode().value())))
                .bodyToMono(Map.class);
    }
}
