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

import static com.example.aggregation.client.WebClientConfig.TRACKING_WEB_CLIENT;

@Component
@RequiredArgsConstructor
public class TrackingClient {

    @Qualifier(TRACKING_WEB_CLIENT)
    private final WebClient webClient;

    public Mono<Map> getTrackingQueue(List<String> tracking) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/track/")
                        .queryParam("q", tracking)
                        .build())
                .retrieve()
                .onStatus(status -> status.value() == HttpStatus.SERVICE_UNAVAILABLE.value(),
                        response -> Mono.error(new ServiceException("Tracking Service Unavailable.", response.statusCode().value()))).bodyToMono(Map.class);
    }
}
