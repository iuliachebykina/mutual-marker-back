package ru.urfu.mutual_marker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Set;

@Value
@Builder
public class TaskFullInfo {

    @Schema(name = "ИД задания", example = "1")
    Long id;

    @Schema(name = "Название задания", example = "Анализ ЦА")
    String title;

    @Schema(name = "Описание задания", description = "Что надо сделать и какая-нибудь инструкция", example = "Проанализировать и сделать схему")
    String description;

    @Schema(name = "Дата старта", description = "Когда задание началось", example = "2007-12-03T10:15:30")
    LocalDateTime openDate;

    @Schema(name = "Дедлайн", description = "До когда надо сделать задание", example = "2007-12-03T10:15:30")
    LocalDateTime closeDate;

    @Schema(name = "ИД комнаты", description = "Какой комнате принадлежит", example = "3")
    Long roomId;

    @Schema(name = "Минимальное количество оценок", description = "Минимальное кол-во оцененных заданий", example = "5")
    int minNumberOfGraded;

    @Schema(name = "Ступени оценивания", description = "Этапы всестороннего оценивания задания")
    Set<MarkStep> markSteps;

    @Schema(name = "Статус жизни")
    Boolean deleted;

    @Value
    public static class MarkStep {

        @Schema(name = "ИД шага оценивания", example = "1")
        Long id;

        @Schema(name = "Заголовок шага", example = "Наличие файла")
        String title;

        @Schema(name = "Описание шага", description = "Что должно быть в работе", example = "Файл загружен")
        String description;

        @Schema(name = "Градация оценки", description = "Как можно оценить")
        Set<Integer> values;

        @Schema(name = "Статус жизни")
        Boolean deleted;
    }
}
