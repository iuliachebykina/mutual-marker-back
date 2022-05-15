package ru.urfu.mutual_marker.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddMarkDto {
    Long projectId;
    Long profileId;
    String comment;
    List<Integer> markStepValues;
}
