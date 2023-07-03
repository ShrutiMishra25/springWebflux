package com.example.aggregation.config;

import com.example.aggregation.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class ShippingSchedulingConfig implements SchedulingConfigurer {

    @Autowired
    private ShippingService shippingService;

    @Bean
    public Executor shippingTaskExecutor() {
        return Executors.newScheduledThreadPool(1);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(shippingTaskExecutor());
        taskRegistrar.addTriggerTask(new Runnable() {
            @Override
            public void run() {
                if (shippingService.shippingEntryTime != null) {
                    shippingService.getShippingDetails();
                }
            }
        }, new Trigger() {
            @Override
            public Instant nextExecution(TriggerContext triggerContext) {
                Duration d = Duration.ofSeconds(5);
                if (shippingService.shippingEntryTime != null) return shippingService.shippingEntryTime.plus(d);
                else return Instant.now().plus(d);
            }
        });
    }

}

