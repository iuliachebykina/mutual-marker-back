package ru.urfu.mutual_marker.service.statistics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatisticsDto {
    String project;
    String group;
    String fullName;
    String mark;
    Long projectId;
    Boolean anomalyDetected;
}
