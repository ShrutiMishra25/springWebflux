package com.example.aggregation.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class WebClientConfigTest {
@Autowired
ApplicationContext context;
    @Test
    void pricingClient() {
        Assertions.assertTrue(context.containsBean("pricingWebClient"));
    }

    @Test
    void shippingWebClient() {
        Assertions.assertTrue(context.containsBean("shippingWebClient"));

    }

    @Test
    void trackingWebClient() {
        Assertions.assertTrue(context.containsBean("trackingWebClient"));

    }
}