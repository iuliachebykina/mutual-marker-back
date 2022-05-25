package ru.urfu.mutual_marker.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.urfu.mutual_marker.jpa.entity.value_type.Name;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class ProjectFinalMarkDto {
    Name studentName;
    String projectTitle;
    Long projectId;
    Long profileId;
    Double finalMark;
}
