package ru.urfu.mutual_marker.dto.mark;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarkFeedbackDto {
    @Schema(title = "Оценка")
    MarkDto mark;
    @Schema(title = "Все отзывы по данной оценке")
    List<MarkStepFeedbackDto> feedbacks;
}
