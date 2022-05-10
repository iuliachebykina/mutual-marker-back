package ru.urfu.mutual_marker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.util.Set;

@Value
public class ProjectCreationInfo {

    @Schema(name = "Название проекта")
    String title;

    @Schema(name = "Описание проекта")
    String description;

    @Schema(name = "Вложения")
    Set<String> attachments;
}
