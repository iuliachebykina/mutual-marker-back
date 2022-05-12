package ru.urfu.mutual_marker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.util.Set;

@Value
public class ProjectCreationInfo {

    @Schema(title = "Название проекта")
    String title;

    @Schema(title = "Описание проекта")
    String description;

    @Schema(title = "Вложения")
    Set<String> attachments;
}
