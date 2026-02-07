package ru.post.PostApp.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
public class PostItemRequest implements Serializable {

    @NotBlank(message = "Тип почтового отправления обязателен")
    @Pattern(regexp = "LETTER|PARCEL|REGISTERED", message = "Недопустимый тип")
    private String type;

    @NotNull(message = "Информация об отправителе обязательна")
    @Valid
    private PostalPartyRequest sender;

    @NotNull(message = "Информация о получателе обязательна")
    @Valid
    private PostalPartyRequest receiver;

    @NotNull(message = "Код почтового подразделения обязателен")
    private String postOfficeCode;

    @NotBlank(message = "ID оператора обязателен")
    private String operatorId;

    @NotBlank(message = "Исходная система обязательна")
    private String sourceSystem;

    @NotNull(message = "Дата и время принятия обязательны")
    @PastOrPresent(message = "Дата принятия не может быть в будущем")
    private LocalDateTime acceptedAt;

}
