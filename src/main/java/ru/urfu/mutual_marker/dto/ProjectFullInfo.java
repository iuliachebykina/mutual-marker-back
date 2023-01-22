package ru.urfu.mutual_marker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.util.Set;

@Data
@Setter
@AllArgsConstructor
public class ProjectFullInfo {

    @Schema(title = "ИД проекта")
    Long id;

    @Schema(title = "Название проекта")
    String title;

    @Schema(title = "Описание проекта")
    String description;

    @Schema(title = "Оценка")
    Double mark;

    @Schema(title = "Вложения")
    Set<String> attachments;
}
