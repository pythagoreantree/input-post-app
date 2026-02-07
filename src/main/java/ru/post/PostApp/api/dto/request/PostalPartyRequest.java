package ru.post.PostApp.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PostalPartyRequest implements Serializable {

    @NotBlank(message = "Имя обязательно")
    @Size(min = 1, max = 100, message = "Имя должно быть от 1 до 100 символов")
    private String firstName;

    @Size(max = 100, message = "Отчество не должно превышать 100 символов")
    private String middleName;

    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 1, max = 100, message = "Фамилия должна быть от 1 до 100 символов")
    private String lastName;

    @NotBlank(message = "Почтовый индекс обязателен")
    @Pattern(regexp = "\\d{6}", message = "Индекс должен состоять из 6 цифр")
    String postalCode;

    @NotBlank(message = "Город обязателен")
    @Size(min = 2, max = 100, message = "Название города должно быть от 2 до 100 символов")
    String city;

    @NotBlank(message = "Улица обязательна")
    @Size(min = 2, max = 200, message = "Название улицы должно быть от 2 до 200 символов")
    String street;

    @NotBlank(message = "Номер дома обязателен")
    String house;

    @Size(max = 10, message = "Корпус не должен превышать 10 символов")
    String building;

    @Size(max = 10, message = "Номер квартиры не должен превышать 10 символов")
    String apartment;

    public String getFullName() {
        StringBuilder sb = new StringBuilder(firstName);
        if (middleName != null && !middleName.isEmpty()) {
            sb.append(" ").append(middleName);
        }
        if (lastName != null && !lastName.isEmpty()) {
            sb.append(" ").append(lastName);
        }
        return sb.toString();
    }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder(postalCode);

        sb.append(city).append(", ");
        sb.append(street).append(", ");
        sb.append("д. ").append(house);

        if (building != null && !building.isBlank()) {
            sb.append(", корп. ").append(building);
        }

        if (apartment != null && !apartment.isBlank()) {
            sb.append(", кв. ").append(apartment);
        }

        return sb.toString();
    }

}
