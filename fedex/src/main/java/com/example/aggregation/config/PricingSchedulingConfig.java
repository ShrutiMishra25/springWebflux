package com.example.aggregation.config;

import com.example.aggregation.service.PricingService;
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
public class PricingSchedulingConfig implements SchedulingConfigurer {

    private final PricingService pricingService;

    @Bean
    public Executor pricingTaskExecutor() {
        return Executors.newScheduledThreadPool(1);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(pricingTaskExecutor());
        taskRegistrar.addTriggerTask(() -> {
            if (pricingService.entryTime != null) {
                pricingService.getPricingDetails();
            }
        }, triggerContext -> {
            Duration d = Duration.ofSeconds(5);
            if (pricingService.entryTime != null) return pricingService.entryTime.plus(d);
            else return Instant.now().plus(d);
        });
    }
}

