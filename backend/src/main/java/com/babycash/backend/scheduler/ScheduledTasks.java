package com.babycash.backend.scheduler;

import com.babycash.backend.service.ILoyaltyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled tasks for the application
 * Currently handles loyalty points expiration
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final ILoyaltyService loyaltyService;

    /**
     * Process expired loyalty points daily at 1 AM
     * Cron: second minute hour day month weekday
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processExpiredLoyaltyPoints() {
        log.info("Starting scheduled task: Process expired loyalty points");
        
        try {
            loyaltyService.processExpiredPoints();
            log.info("Successfully completed expired loyalty points processing");
        } catch (Exception e) {
            log.error("Error processing expired loyalty points", e);
        }
    }
}
