package com.example.aggregation.client;

import com.example.aggregation.model.AggregationDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class Client {
    private static final Logger LOG = Logger.getLogger(Client.class.getName());
    WebClient webClient = WebClient.create("http://localhost:8080");


    public Flux<AggregationDetails> fetchAggregationDetails(List<String> pricing, List<String> track, List<String> shipments) {
        var pricingDetails = getPricing(pricing);
        var tracking = getTracking(track);
        var shipment = getShipment(shipments);
        var aggregationDetailsFlux = Flux.zip(pricingDetails, tracking, shipment).flatMap(details -> Flux.just(new AggregationDetails(
                details.getT1(), details.getT2(), details.getT3())));
        return aggregationDetailsFlux;
    }

    private Flux<Map> getPricing(List<String> pricing) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/pricing/")
                .queryParam("q", pricing)
                .build()).retrieve().bodyToFlux(Map.class);


    }

    private Flux<Map> getTracking(List<String> tracking) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/track/")
                .queryParam("q", tracking)
                .build()).retrieve().bodyToFlux(Map.class);
    }

    private Flux<Map> getShipment(List<String> shipment) {
        return webClient.get().uri(uriBuilder -> uriBuilder.path("/shipments/")
                .queryParam("q", shipment)
                .build()).retrieve().bodyToFlux(Map.class);
    }
    public Map<String, Double> getPricingQueue(List<String> pricing) {
        Map<String, Double> map3 = new HashMap<>();

        Flux<Map> result = webClient.get().uri(uriBuilder -> uriBuilder.path("/pricing")
                .queryParam("q", pricing)
                .build()).retrieve().bodyToFlux(Map.class);
        result.subscribe(map3::putAll);
        return map3;
    }
}
