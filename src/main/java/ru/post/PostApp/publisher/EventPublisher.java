package ru.post.PostApp.publisher;

import ru.post.PostApp.domain.dto.EventDestination;

public interface EventPublisher<T extends EventDestination> {

    public void publish(T destination, Object event);

}
