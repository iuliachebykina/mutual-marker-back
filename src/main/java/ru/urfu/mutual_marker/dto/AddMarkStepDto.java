package ru.urfu.mutual_marker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddMarkStepDto {
    Long profileId;
    Long taskId;
    List<Integer> values;
    String description;
    String title;
}
