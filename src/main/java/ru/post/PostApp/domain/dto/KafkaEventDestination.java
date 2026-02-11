package ru.post.PostApp.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KafkaEventDestination implements EventDestination {
}
