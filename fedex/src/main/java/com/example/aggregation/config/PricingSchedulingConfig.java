package com.example.aggregation.config;

import com.example.aggregation.service.PricingService;
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
public class PricingSchedulingConfig implements SchedulingConfigurer {

    @Autowired
    private PricingService pricingService;

    @Bean
    public Executor pricingTaskExecutor() {
        return Executors.newScheduledThreadPool(1);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(pricingTaskExecutor());
        taskRegistrar.addTriggerTask(new Runnable() {
            @Override
            public void run() {
                if (pricingService.entryTime != null) {
                    pricingService.getPricingDetails();
                }
            }
        }, new Trigger() {
            @Override
            public Instant nextExecution(TriggerContext triggerContext) {
                Duration d = Duration.ofSeconds(5);
                if (pricingService.entryTime != null) return pricingService.entryTime.plus(d);
                else return Instant.now().plus(d);
            }
        });
    }

}

