package ru.urfu.mutual_marker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationInfo {
    @Schema(title = "Почта", example = "hi@gmail.com", required = true)
    String email;
    @Schema(title = "Пароль", required = true)
    String password;
    @Schema(title = "Номер телефона", example = "88005553535")
    String phoneNumber;
    @Schema(title = "Имя", example = "Петр", required = true)
    String firstName;
    @Schema(title = "Фамилия", example = "Петров", required = true )
    String lastName;
    @Schema(title = "Отчество", example = "Петрович")
    String patronymic;
    @Schema(title = "Университет", example = "УрФУ")
    String university;
    @Schema(title = "Институт", example = "ИРИТ-РтФ")
    String institute;
    @Schema(title = "Студенческая группа", example = "РИ-390013")
    String studentGroup;
    @Schema(title = "Ссылка на соц. сеть", example = "vk.com/irit")
    String socialNetwork;
    @Schema(title = "Предмет преподавателя", example = "Математика")
    String subject;
}
