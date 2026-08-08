package ru.mustafa.messenger.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.mustafa.messenger.repository.MessageRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AnalyticsService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsService.class);
    private final MessageRepository messageRepository;

    public AnalyticsService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void calculateAndLogDailyStats() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        long totalMessagesToday = messageRepository.countByCreatedAtAfter(startOfToday);

        log.info("=== ЕЖЕДНЕВНЫЙ ОТЧЕТ ===");
        log.info("Дата: {}", LocalDate.now());
        log.info("Всего отправлено сообщений за сутки: {}", totalMessagesToday);
        log.info("========================");
    }
}