package ru.post.PostApp.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.post.PostApp.publisher.EventPublisher;
import ru.post.PostApp.publisher.KafkaEventPublisher;
import ru.post.PostApp.publisher.RabbitEventPublisher;

@Configuration
public class EventPublisherConfig {

    @Bean
    @ConditionalOnProperty(name = "events.broker", havingValue = "rabbit", matchIfMissing = true)
    public EventPublisher rabbitEventPublisher(RabbitTemplate rabbitTemplate) {
        return new RabbitEventPublisher(rabbitTemplate);
    }

    @Bean
    @ConditionalOnProperty(name = "events.broker", havingValue = "kafka")
    public EventPublisher kafkaEventPublisher() {
        return new KafkaEventPublisher();
    }
}
