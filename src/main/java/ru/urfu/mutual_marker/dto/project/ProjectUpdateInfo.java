package ru.urfu.mutual_marker.dto.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Value
public class ProjectUpdateInfo {

    @Schema(title = "ID проекта", example = "1")
    Long id;

    @Schema(title = "Название проекта", example = "Проект")
    String title;

    @Schema(title = "Описание проекта")
    String description;
}
