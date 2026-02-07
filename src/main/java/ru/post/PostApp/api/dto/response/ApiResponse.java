package ru.post.PostApp.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime timestamp;

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
}
