package edu.konditer.workfinder_statistics.listeners;

import edu.konditer.events.VacancyCreatedEvent;
import edu.konditer.events.VacancyDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class VacancyEventListener {

    private static final Logger log = LoggerFactory.getLogger(VacancyEventListener.class);

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "notification-queue", durable = "true"),
            exchange = @Exchange(name = "vacancies-exchange", type = "topic"),
            key = "vacancy.created"
    ))
    public void handleVacancyCreatedEvent(VacancyCreatedEvent event) {
        log.info("Received new vacancy created event: {}.", event);
        // Логика сбора статистики
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "notification-queue", durable = "true"),
            exchange = @Exchange(name = "vacancies-exchange", type = "topic"),
            key = "vacancy.deleted"
    ))
    public void handleVacancyDeletedEvent(VacancyDeletedEvent event) {
        log.info("Received new vacancy deleted event: {}.", event);
        // Логика сбора статистики
    }
}

