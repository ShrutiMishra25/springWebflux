package com.example.aggregation.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    public static final String PRICING_WEB_CLIENT = "pricingWebClient";
    public static final String SHIPPING_WEB_CLIENT = "shippingWebClient";
    public static final String TRACKING_WEB_CLIENT = "trackingWebClient";

    @Value("${pricing.base.url:http://localhost:8080}")
    private String pricingBaseUrl;
    @Value("${shipping.base.url:http://localhost:8080}")
    private String shippingBaseUrl;
    @Value("${tracking.base.url:http://localhost:8080}")
    private String trackingBaseUrl;

    @Bean(PRICING_WEB_CLIENT)
    public WebClient pricingClient() {
        return WebClient.builder().baseUrl(pricingBaseUrl).build();
    }

    @Bean(SHIPPING_WEB_CLIENT)
    public WebClient shippingWebClient() {
        return WebClient.builder().baseUrl(shippingBaseUrl).build();
    }

    @Bean(TRACKING_WEB_CLIENT)
    public WebClient trackingWebClient() {
        return WebClient.builder().baseUrl(trackingBaseUrl).build();
    }
}
