package ru.urfu.mutual_marker.dto.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectCreationResultDto {
    Long id;
    Boolean isOverdue;
}
