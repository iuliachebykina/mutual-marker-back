package ru.urfu.mutual_marker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;

@Value
public class TaskCreationRequest {

    @NotBlank
    @Schema(title = "Название задания", example = "Анализ ЦА", required = true)
    String title;

    @Schema(title = "Описание задания", description = "Что надо сделать и какая-нибудь инструкция",
            example = "Проанализировать и сделать схему")
    String description;

    @NotNull
    @FutureOrPresent
    @Schema(title = "Дата старта", description = "Когда задание открылось", example = "2007-12-03T10:15:30")
    LocalDateTime openDate = LocalDateTime.now();

    @NotNull
    @Future
    @Schema(title = "Дедлайн", description = "До когда надо сделать задание", example = "2007-12-03T10:15:30", required = true)
    LocalDateTime closeDate;

    @Min(1)
    @Schema(title = "ИД комнаты", description = "Какой комнате принадлежит", example = "3", required = true)
    Long roomId;

    @Min(1)
    @Schema(title = "Минимальное количество оценок", description = "Минимальное кол-во оцененных заданий", example = "5", required = true)
    int minNumberOfGraded;

    @NotEmpty
    @Schema(title = "Ступени оценивания", description = "Этапы всестороннего оценивания задания", required = true)
    Set<MarkStep> markSteps;

    @NotBlank
    @Email
    @Schema(title = "Создатель", example = "example@mail.com")
    String owner;

    @Schema(title = "Имена вложений", example = "example.pdf")
    Set<String> attachments;

    @Value
    public static class MarkStep {

        @NotBlank
        @Schema(title = "Заголовок шага", example = "Наличие файла", required = true)
        String title;

        @NotBlank
        @Schema(title = "Описание шага", description = "Что должно быть в работе", example = "Файл загружен", required = true)
        String description;

        @NotEmpty
        @Schema(title = "Градация оценки", description = "Как можно оценить", required = true)
        Set<Integer> values;
    }
}
