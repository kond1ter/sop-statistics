package edu.konditer.workfinder_statistics.listeners;

import edu.konditer.events.VacancyCreatedEvent;
import edu.konditer.events.VacancyDeletedEvent;
import edu.konditer.workfinder_statistics.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import java.io.IOException;

@Component
public class VacancyEventListener {
    private static final Logger log = LoggerFactory.getLogger(VacancyEventListener.class);
    private static final String EXCHANGE_NAME = "vacancy-exchange";
    private static final String QUEUE_NAME_VACANCY_CREATED = "vacancy-created-queue-statistics";
    private static final String QUEUE_NAME_VACANCY_DELETED = "vacancy-deleted-queue-statistics";
    
    private final StatisticsService statisticsService;

    public VacancyEventListener(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = QUEUE_NAME_VACANCY_CREATED,
                            durable = "true",
                            arguments = {
                                    @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                                    @Argument(name = "x-dead-letter-routing-key", value = "dlq.notifications")
                            }),
                    exchange = @Exchange(name = EXCHANGE_NAME, type = "topic", durable = "true"),
                    key = "vacancy.created"
            )
    )
    public void handleVacancyCreatedEvent(@Payload VacancyCreatedEvent event, Channel channel,
                                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            log.info("Received VacancyCreatedEvent: {}", event);
            if (event.title() != null && event.title().equalsIgnoreCase("CRASH")) {
                throw new RuntimeException("Simulating processing error for DLQ test");
            }
            
            // Обновляем статистику после создания вакансии
            statisticsService.updateStatistics();
            
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("Failed to process event: {}. Sending to DLQ.", event, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = QUEUE_NAME_VACANCY_DELETED,
                            durable = "true",
                            arguments = {
                                    @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                                    @Argument(name = "x-dead-letter-routing-key", value = "dlq.notifications")
                            }),
                    exchange = @Exchange(name = EXCHANGE_NAME, type = "topic", durable = "true"),
                    key = "vacancy.deleted"
            )
    )
    public void handleVacancyDeletedEvent(@Payload VacancyDeletedEvent event, Channel channel,
                                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            log.info("Received VacancyDeletedEvent: {}", event);
            
            // Обновляем статистику после удаления вакансии
            statisticsService.updateStatistics();
            
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("Failed to process event: {}. Sending to DLQ.", event, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "notification-queue.dlq", durable = "true"),
                    exchange = @Exchange(name = "dlx-exchange", type = "topic", durable = "true"),
                    key = "dlq.notifications"
            )
    )
    public void handleDlqMessages(Object failedMessage) {
        log.error("!!! Received message in DLQ: {}", failedMessage);
    }
}
