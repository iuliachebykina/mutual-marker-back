package ru.urfu.mutual_marker.dto.mark;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Value
public class MarkDto {
    Double coefficient;
    Boolean isTeacherMark;
    Integer finalMark;
    String comment;
    List<MarkStepDto> markSteps;
}
