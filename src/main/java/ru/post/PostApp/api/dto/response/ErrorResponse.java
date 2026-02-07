package ru.post.PostApp.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

public class ErrorResponse {
    private boolean success = false;
    private String errorCode;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> errors;

    public ErrorResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorResponse(String errorCode, String message, Map<String, String> errors) {
        this.errorCode = errorCode;
        this.message = message;
        this.errors = errors;
    }

}