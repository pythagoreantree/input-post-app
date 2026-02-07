package ru.post.PostApp.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.post.PostApp.api.dto.request.PostItemRequest;
import ru.post.PostApp.api.dto.response.PostItemResponse;
import ru.post.PostApp.config.RabbitMQConfig;
import ru.post.PostApp.domain.document.PostItemDocument;
import ru.post.PostApp.repository.mongo.PostItemMongoRepository;

import java.time.LocalDateTime;
import java.util.UUID;

import static ru.post.PostApp.domain.document.PostItemDocument.fromRequest;

@Service
public class PostItemService {

    public static final String POST_CREATED = "POST_CREATED";

    @Autowired
    private PostItemMongoRepository postItemRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * Создание почтового отправления из DTO
     */
    public PostItemResponse createPostItem(PostItemRequest request) {

        PostItemDocument document = fromRequest(request);

        PostItemDocument.OutboxEvent event = PostItemDocument.OutboxEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(POST_CREATED)
                .payload(request)
                .createdAt(LocalDateTime.now())
                .build();

        document.getPendingEvents().add(event);

        PostItemDocument savedDocument = postItemRepository.save(document);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                event
        );

        return toResponse(savedDocument);
    }

    /**
     * Преобразование документа в Response DTO
     */
    private PostItemResponse toResponse(PostItemDocument document) {
        PostItemResponse response = new PostItemResponse();
        response.setId(document.getId());
        response.setStatus(document.getStatus());
        return response;
    }
}