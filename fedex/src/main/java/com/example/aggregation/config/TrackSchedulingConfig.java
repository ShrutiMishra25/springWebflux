package com.example.aggregation.config;

import com.example.aggregation.service.TrackingService;
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
public class TrackSchedulingConfig implements SchedulingConfigurer {

    private final TrackingService trackingService;

    @Bean
    public Executor trackTaskExecutor() {
        return Executors.newScheduledThreadPool(1);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(trackTaskExecutor());
        taskRegistrar.addTriggerTask(() -> {
            if (trackingService.trackingEntryTime != null) {
                trackingService.getTrackingDetails();
            }
        }, triggerContext -> {
            Duration d = Duration.ofSeconds(5);
            if (trackingService.trackingEntryTime != null) return trackingService.trackingEntryTime.plus(d);
            else return Instant.now().plus(d);
        });
    }

}

