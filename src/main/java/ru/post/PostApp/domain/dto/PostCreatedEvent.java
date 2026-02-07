package ru.post.PostApp.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCreatedEvent implements Serializable {

    String id;

    String type;

    LocalDateTime createdAt;

    Object payload;

}
