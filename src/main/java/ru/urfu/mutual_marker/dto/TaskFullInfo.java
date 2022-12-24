package ru.urfu.mutual_marker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Set;

@Value
@Builder
public class TaskFullInfo {

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

    @Schema(title = "Минимальное количество оценок", description = "Минимальное кол-во оцененных заданий", example = "5")
    int minNumberOfGraded;

    @Schema(title = "Ступени оценивания", description = "Этапы всестороннего оценивания задания")
    Set<MarkStep> markSteps;

    @Schema(title = "Статус жизни")
    Boolean deleted;

    @Schema(title = "Вложения")
    Set<String> attachments;

    @Value
    public static class MarkStep {

        @Schema(title = "ИД шага оценивания", example = "1")
        Long id;

        @Schema(title = "Заголовок шага", example = "Наличие файла")
        String title;

        @Schema(title = "Описание шага", description = "Что должно быть в работе", example = "Файл загружен")
        String description;

        @Schema(title = "Градация оценки", description = "Как можно оценить")
        Set<Integer> values;

        @Schema(title = "Статус жизни")
        Boolean deleted;
    }
}
