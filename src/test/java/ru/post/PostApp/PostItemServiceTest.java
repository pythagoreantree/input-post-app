package ru.post.PostApp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.post.PostApp.api.dto.request.PostItemRequest;
import ru.post.PostApp.api.dto.request.PostalPartyRequest;
import ru.post.PostApp.api.dto.response.PostItemResponse;
import ru.post.PostApp.domain.document.PostItemDocument;
import ru.post.PostApp.domain.dto.PostCreatedEvent;
import ru.post.PostApp.domain.dto.RabbitEventDestination;
import ru.post.PostApp.publisher.EventPublisher;
import ru.post.PostApp.repository.PostItemMongoRepository;
import ru.post.PostApp.service.PostItemService;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostItemServiceTest {

    @Mock
    private PostItemMongoRepository postItemRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private PostItemService postItemService;

    private PostItemRequest request;
    private PostItemDocument savedDocument;

    @BeforeEach
    void setUp() {
        // Подготовим тестовые данные
        request = PostItemRequest.builder()
                .type("LETTER")
                .userId(UUID.randomUUID())
                .sender(PostalPartyRequest.builder()
                        .firstName("Григорий")
                        .middleName("Валентинович")
                        .lastName("Белов")
                        .postalCode("946481")
                        .city("Мытищи")
                        .street("Восточная пр.")
                        .house("212")
                        .build())
                .receiver(PostalPartyRequest.builder()
                        .firstName("Евгений")
                        .middleName("Егорович")
                        .lastName("Дорофеев")
                        .postalCode("947720")
                        .city("Тверь")
                        .street("улица Степная")
                        .house("990")
                        .building("1")
                        .build())
                .postOfficeCode("MOSCOW-045")
                .operatorId("OP-54321")
                .sourceSystem("MOBILE_APP")
                .acceptedAt(LocalDateTime.now())
                .build();

        savedDocument = PostItemDocument.fromRequest(request);
        savedDocument.setId("test-doc-id");
    }

    @Test
    void shouldCreatePostItemSuccessfully() {
        // given
        when(postItemRepository.save(any(PostItemDocument.class))).thenReturn(savedDocument);

        doNothing().when(eventPublisher).publish(any(RabbitEventDestination.class), any(PostCreatedEvent.class));

        // when
        PostItemResponse response = postItemService.createPostItem(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(savedDocument.getId());
        assertThat(response.getStatus()).isEqualTo(savedDocument.getStatus());

        // Проверяем, что save вызывался два раза
        verify(postItemRepository, times(2)).save(any(PostItemDocument.class));

        // Проверяем, что событие опубликовалось
        verify(eventPublisher, times(1)).publish(
                any(RabbitEventDestination.class),
                any(PostCreatedEvent.class)
        );

        // Проверяем, что у сохранённого документа проставился статус SENT
        verify(postItemRepository, times(1)).save(argThat((PostItemDocument document) ->
                document.getPendingEvents().stream()
                        .anyMatch(event -> PostItemService.SENT.equals(event.getStatus()))
        ));
    }

    @Test
    void shouldCreatePostItemEvenWhenRabbitFails() {
        // given
        when(postItemRepository.save(any(PostItemDocument.class))).thenReturn(savedDocument);

        // При публикации кидаем исключение (RabbitMQ упал)
        doThrow(new RuntimeException("RabbitMQ connection failed"))
                .when(eventPublisher).publish(any(RabbitEventDestination.class), any(PostCreatedEvent.class));

        // when
        PostItemResponse response = postItemService.createPostItem(request);

        // then
        // Проверяем, что метод всё равно вернул ответ (не упал)
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(savedDocument.getId());
        assertThat(response.getStatus()).isEqualTo(savedDocument.getStatus());

        // Проверяем, что save вызывался ТОЛЬКО ОДИН раз
        // (второго save с обновлением статуса быть не должно, так как RabbitMQ упал)
        verify(postItemRepository, times(1)).save(any(PostItemDocument.class));

        // Проверяем, что событие пыталось опубликоваться
        verify(eventPublisher, times(1))
                .publish(any(RabbitEventDestination.class), any(PostCreatedEvent.class));

        // Проверяем, что статус события НЕ стал SENT
        verify(postItemRepository, times(1)).save(argThat((PostItemDocument document) -> {
            return document.getPendingEvents().stream()
                    .noneMatch(event -> PostItemService.SENT.equals(event.getStatus()));
        }));
    }

    @Test
    void shouldCreateEventWithCorrectType() {
        // given
        when(postItemRepository.save(any(PostItemDocument.class))).thenReturn(savedDocument);

        ArgumentCaptor<PostCreatedEvent> eventCaptor = ArgumentCaptor.forClass(PostCreatedEvent.class);
        doNothing().when(eventPublisher).publish(any(), eventCaptor.capture());

        // when
        postItemService.createPostItem(request);

        // then
        PostCreatedEvent publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent.getType()).isEqualTo(PostItemService.POST_CREATED);
        assertThat(publishedEvent.getPayload()).isEqualTo(request);
        assertThat(publishedEvent.getId()).isNotBlank();
    }
}
