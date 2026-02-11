package ru.post.PostApp.publisher;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import ru.post.PostApp.domain.dto.RabbitEventDestination;

import java.util.UUID;

public class RabbitEventPublisher implements EventPublisher<RabbitEventDestination> {

    private final RabbitTemplate rabbitTemplate;

    public RabbitEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(RabbitEventDestination destination, Object event) {
        String messageId = UUID.randomUUID().toString();

        CorrelationData correlationData = new CorrelationData(messageId);

        rabbitTemplate.convertAndSend(
                destination.getExchange(),
                destination.getRoutingKey(),
                event,
                correlationData
        );
    }

}
