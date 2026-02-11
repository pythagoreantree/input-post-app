package ru.post.PostApp.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RabbitEventDestination implements EventDestination {

    private String exchange;

    private String routingKey;

}

