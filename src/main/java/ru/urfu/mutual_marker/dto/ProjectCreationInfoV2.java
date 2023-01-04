package ru.urfu.mutual_marker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Value
public class ProjectCreationInfoV2 {
    @Schema(title = "Название проекта")
    String title;

    @Schema(title = "Описание проекта")
    String description;

    @Schema(title = "Вложения")
    AttachmentDto[] attachments;
}
