package com.example.aggregation.client;

import com.example.aggregation.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static com.example.aggregation.client.WebClientConfig.PRICING_WEB_CLIENT;

@Component
@RequiredArgsConstructor
public class PricingClient {

    @Qualifier(PRICING_WEB_CLIENT)
    private final WebClient webClient;

    public Mono<Map> getPricingQueue(List<String> pricing) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/pricing/")
                        .queryParam("q", pricing)
                        .build()).retrieve()
                .onStatus(status -> status.value() == HttpStatus.SERVICE_UNAVAILABLE.value(),
                        response -> Mono.error(new ServiceException("Pricing Service Unavailable.", response.statusCode().value())))
                .bodyToMono(Map.class);
    }
}
