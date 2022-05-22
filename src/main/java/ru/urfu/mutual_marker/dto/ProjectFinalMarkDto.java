package ru.urfu.mutual_marker.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class ProjectFinalMarkDto {
    String projectTitle;
    Long projectId;
    Long profileId;
    Double finalMark;
}
