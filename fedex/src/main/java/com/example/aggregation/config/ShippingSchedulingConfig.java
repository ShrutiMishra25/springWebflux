package com.example.aggregation.config;

import com.example.aggregation.service.ShippingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ShippingSchedulingConfig implements SchedulingConfigurer {


    private final ShippingService shippingService;

    @Bean
    public Executor shippingTaskExecutor() {
        return Executors.newScheduledThreadPool(1);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(shippingTaskExecutor());
        taskRegistrar.addTriggerTask(() -> {
            if (shippingService.shippingEntryTime != null) {
                shippingService.getShippingDetails();
            }
        }, triggerContext -> {
            Duration d = Duration.ofSeconds(5);
            if (shippingService.shippingEntryTime != null) return shippingService.shippingEntryTime.plus(d);
            else return Instant.now().plus(d);
        });
    }

}
