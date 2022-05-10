package ru.urfu.mutual_marker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChangePassword {
    @NotBlank
    @Schema(title = "Почта", example = "user@gmail.com", required = true)
    String email;
    @NotBlank
    @Schema(title = "Старый пароль", example = "qwertyOld", required = true)
    String oldPassword;
    @NotBlank
    @Schema(title = "Новый пароль", example = "qwertyNew", required = true)
    String newPassword;
}
