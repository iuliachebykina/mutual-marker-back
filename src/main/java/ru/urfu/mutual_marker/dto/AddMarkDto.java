package ru.urfu.mutual_marker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddMarkDto {
    @Schema(title = "Id проекта для добавления", required = true)
    Long projectId;
    @Schema(title = "Id добавляющего", required = true)
    Long profileId;
    @Schema(title = "Отзыв к оценке")
    String comment;
    @Schema(title = "Выставленные значения для каждого шага оценки", required = true)
    List<Integer> markStepValues;
}
