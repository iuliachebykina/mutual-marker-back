package ru.urfu.mutual_marker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Value
public class MarkDto {
    Double coefficient;
    Boolean isTeacherMark;
    Integer markValue;
    String comment;
}
