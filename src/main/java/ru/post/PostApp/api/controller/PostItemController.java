package ru.post.PostApp.api.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.post.PostApp.api.dto.request.PostItemRequest;
import ru.post.PostApp.api.dto.response.ApiResponse;
import ru.post.PostApp.api.dto.response.ErrorResponse;
import ru.post.PostApp.api.dto.response.PostItemResponse;
import ru.post.PostApp.service.PostItemService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/post-items")
@CrossOrigin(origins = "*")
public class PostItemController {

    private static final Logger log = LoggerFactory.getLogger(PostItemController.class);

    @Autowired
    private PostItemService postItemService;


    @PostMapping
    public ResponseEntity<?> createPostItem(
            @Valid @RequestBody PostItemRequest request,
            BindingResult bindingResult) {

        log.info("Получен запрос на создание почтового отправления: тип={}", request.getType());

        // Валидация
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            log.warn("Ошибка валидации: {}", errors);
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", "Ошибка валидации", errors));
        }

        try {
            PostItemResponse response = postItemService.createPostItem(request);

            log.info("Почтовое отправление создано успешно: id={}", response.getId());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Почтовое отправление создано успешно", response));

        } catch (Exception e) {
            log.error("Ошибка сервера при создании отправления: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("SERVER_ERROR", "Внутренняя ошибка сервера"));
        }
    }

    /**
     * Проверка здоровья API
     * GET http://localhost:8080/api/v1/post-items/health
     */
/*    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "post-items-api");
        health.put("timestamp", LocalDateTime.now());
        health.put("database", postItemService.isDatabaseConnected() ? "CONNECTED" : "DISCONNECTED");

        return ResponseEntity.ok(health);
    }*/
}