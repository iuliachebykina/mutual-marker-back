package ru.urfu.mutual_marker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.urfu.mutual_marker.dto.profileInfo.StudentInfo;

import java.util.Set;

@Data
public class ProjectInfo {

    @Schema(title = "ИД проекта")
    Long id;

    @Schema(title = "Название проекта")
    String title;

    @Schema(title = "Описание проекта")
    String description;

    @Schema(title = "Студент")
    StudentInfo student;

    @Schema(title = "Вложения")
    Set<String> attachments;
}
