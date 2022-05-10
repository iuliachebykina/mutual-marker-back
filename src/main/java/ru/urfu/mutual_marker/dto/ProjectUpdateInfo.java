package ru.urfu.mutual_marker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Value
public class ProjectUpdateInfo {

    @Schema(name = "ID проекта", example = "1")
    Long id;

    @Schema(name = "Название проекта", example = "Проект")
    String title;

    @Schema(name = "Описание проекта")
    String description;
}
