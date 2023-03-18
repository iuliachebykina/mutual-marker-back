package ru.urfu.mutual_marker.dto.mark;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarkStepDto {
    Integer value;
    String comment;
    Long ownerId;
    Long markStepId;
}
