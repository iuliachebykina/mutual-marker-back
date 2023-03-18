package ru.urfu.mutual_marker.dto.mark;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
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
    @Schema(title = "Выставленные значения для каждого шага оценки и отзыв на шаг", required = true)
    List<MarkStepDto> markStepDtos;
}
