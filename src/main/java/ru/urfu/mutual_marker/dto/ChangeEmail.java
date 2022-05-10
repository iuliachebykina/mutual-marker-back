package ru.urfu.mutual_marker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChangeEmail {
    @NotBlank
    @Schema(title = "Старый пароль", example = "oldemail@gmail.com", required = true)
    String oldEmail;
    @NotBlank
    @Schema(title = "Новый пароль", example = "oldemail@gmail.com", required = true)
    String newEmail;
}
