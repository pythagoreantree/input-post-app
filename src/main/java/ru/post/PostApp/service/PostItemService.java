package ru.post.PostApp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.post.PostApp.api.dto.request.PostItemRequest;
import ru.post.PostApp.api.dto.response.PostItemResponse;
import ru.post.PostApp.config.RabbitMQConfig;
import ru.post.PostApp.domain.document.PostItemDocument;
import ru.post.PostApp.domain.dto.PostCreatedEvent;
import ru.post.PostApp.repository.mongo.PostItemMongoRepository;

import java.time.LocalDateTime;
import java.util.UUID;

import static ru.post.PostApp.domain.document.PostItemDocument.fromRequest;

@Slf4j
@Service
public class PostItemService {

    public static final String POST_CREATED = "POST_CREATED";
    public static final String SENT = "SENT";

    @Autowired
    private PostItemMongoRepository postItemRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * Создание почтового отправления из DTO
     */
    public PostItemResponse createPostItem(PostItemRequest request) {

        PostItemDocument document = fromRequest(request);

        PostCreatedEvent rabbitEvent = PostCreatedEvent.builder()
                .id(UUID.randomUUID().toString())
                .type(POST_CREATED)
                .payload(request)
                .createdAt(LocalDateTime.now())
                .build();

        PostItemDocument.OutboxEvent outboxEvent = PostItemDocument.OutboxEvent.builder()
                .id(rabbitEvent.getId())
                .type(rabbitEvent.getType())
                .payload(rabbitEvent.getPayload())
                .createdAt(rabbitEvent.getCreatedAt())
                .build();
        document.getPendingEvents().add(outboxEvent);
        PostItemDocument savedDocument = postItemRepository.save(document);

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY,
                    rabbitEvent
            );

            outboxEvent.setStatus(SENT);
            outboxEvent.setSentAt(LocalDateTime.now());
            postItemRepository.save(savedDocument);

        } catch (Exception e) {
            log.warn("Failed to send to RabbitMQ, will retry later", e);
        }

        return toResponse(savedDocument);
    }

    /**
     * Преобразование документа в Response DTO
     */
    private PostItemResponse toResponse(PostItemDocument document) {
        return PostItemResponse.builder()
                .id(document.getId())
                .status(document.getStatus())
                .build();
    }
}