package ru.post.PostApp.publisher;

import ru.post.PostApp.domain.dto.KafkaEventDestination;

public class KafkaEventPublisher implements EventPublisher<KafkaEventDestination> {

    @Override
    public void publish(KafkaEventDestination destination, Object event) {
        throw new UnsupportedOperationException("Kafka not implemented yet");
    }
}
