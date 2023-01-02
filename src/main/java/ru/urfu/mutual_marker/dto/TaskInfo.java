package ru.urfu.mutual_marker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Setter;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
public class TaskInfo {

    @Schema(title = "ИД задания", example = "1")
    Long id;

    @Schema(title = "Название задания", example = "Анализ ЦА")
    String title;

    @Schema(title = "Описание задания", description = "Что надо сделать и какая-нибудь инструкция", example = "Проанализировать и сделать схему")
    String description;

    @Schema(title = "Дата старта", description = "Когда задание началось", example = "2007-12-03T10:15:30")
    LocalDateTime openDate;

    @Schema(title = "Дедлайн", description = "До когда надо сделать задание", example = "2007-12-03T10:15:30")
    LocalDateTime closeDate;

    @Schema(title = "ИД комнаты", description = "Какой комнате принадлежит", example = "3")
    Long roomId;

    @Schema(title = "Статус жизни", description = "Удалено или нет", example = "false")
    boolean deleted;

    @Schema(title = "Количество работ, которые необходимо оценить", description = "Количество работ, которые необходимо оценить для получения оценкм", example = "1")
    Long numberOfWorksLeftToGrade;
}
