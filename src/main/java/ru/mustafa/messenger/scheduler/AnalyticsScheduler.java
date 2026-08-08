package ru.mustafa.messenger.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.mustafa.messenger.service.AnalyticsService;

@Component
public class AnalyticsScheduler {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsScheduler.class);
    private final AnalyticsService analyticsService;

    public AnalyticsScheduler(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @Async("analyticsExecutor")
    @Scheduled(cron = "0 59 23 * * *")
    public void runDailyAnalyticsTask() {
        log.info("Запущена задача в потоке: {}", Thread.currentThread().getName());
        try {
            analyticsService.calculateAndLogDailyStats();
        } catch (Exception e) {
            log.error("Ошибка при выполнении запланированной задачи: ", e);
        }
    }
}
