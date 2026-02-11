package ru.post.PostApp.api.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostItemResponse {

    private String  id;

    private String status;
}
