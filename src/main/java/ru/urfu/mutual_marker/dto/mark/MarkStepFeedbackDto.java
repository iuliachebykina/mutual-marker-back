package ru.urfu.mutual_marker.dto.mark;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarkStepFeedbackDto {
    @Schema(title = "Оценка за шаг", required = true)
    Integer value;
    @Schema(title = "Отзыв")
    String comment;
    @Schema(title = "Id оставляющего отзыв", required = true)
    Long reviewerId;
    @Schema(title = "Id шага оценки", required = true)
    Long markStepId;
}
