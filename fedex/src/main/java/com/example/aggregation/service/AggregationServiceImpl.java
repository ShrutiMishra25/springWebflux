package com.example.aggregation.service;

import com.example.aggregation.model.AggregationDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AggregationServiceImpl implements AggregationService {

    @Autowired
    private PricingService pricingService;
    @Autowired
    private TrackingService trackingService;
    @Autowired
    private ShippingService shippingService;

    @Override
    public CompletableFuture<AggregationDetails> getAggregationDetails(List<String> pricing, List<String> track, List<String> shipments) {
        CompletableFuture<Map<String, Double>> pricingDetails = CompletableFuture.supplyAsync(() -> pricingService.queryPricing(pricing));

        CompletableFuture<Map<String, String>> trackingDetails = CompletableFuture.supplyAsync(() -> trackingService.queryTracking(track));
        CompletableFuture<Map<String, List<String>>> shippingDetails = CompletableFuture.supplyAsync(() -> shippingService.queryShipping(shipments));

        return CompletableFuture.allOf(pricingDetails, trackingDetails, shippingDetails).thenApplyAsync(aggregation -> {
            AggregationDetails aggregationDetails = new AggregationDetails();

            Map<String, Double> pricingDetail = pricingDetails.join();
            if (pricingDetail != null) {
                aggregationDetails.setPricing(pricingDetail);
            }

            Map<String, List<String>> shippingDetail = shippingDetails.join();
            if (shippingDetail != null) {
                aggregationDetails.setShipments(shippingDetail);
            }

            Map<String, String> trackingDetail = trackingDetails.join();
            aggregationDetails.setTrack(trackingDetail);

            return aggregationDetails;
        });
    }
}
