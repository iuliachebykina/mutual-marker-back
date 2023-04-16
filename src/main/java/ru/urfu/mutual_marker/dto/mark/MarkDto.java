package ru.urfu.mutual_marker.dto.mark;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
//@Value
public class MarkDto {
    @Schema(title = "Коэффициент оценки, используется в оценках преподавателя", required = true)
    Double coefficient;
    @Schema(title = "Является ли оценка выставленной преподавателем", required = true)
    Boolean isTeacherMark;
    @Schema(title = "Сумма выставленных оценок по каждому критерию, НЕ ПРИВЕДЕНА к 100", required = true)
    Double unscaledMark;
    @Schema(title = "Сумма максимальных оценок по каждому критерию", required = true)
    Double maxMarkValue;
    @Schema(title = "Сумма выставленных оценок по каждому критерию, ПРИВЕДЕНА к 100", required = true)
    Double scaledMark;
    @Schema(title = "Отзыв за оценку", required = true)
    String comment;
    @Schema(title = "Отзывы по каждому шагу оценки", required = true)
    List<MarkStepFeedbackDto> markSteps;
}
