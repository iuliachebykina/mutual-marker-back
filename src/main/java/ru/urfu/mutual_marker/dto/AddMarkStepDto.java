package ru.urfu.mutual_marker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(title = "Id создающего преподавателя", required = true)
    Long profileId;
    @Schema(title = "Id задания, к которому привязан шаг оценки", required = true)
    Long taskId;
    @Schema(title = "Список значений шага", example = "[3, 6, 10]", required = true)
    List<Integer> values;
    @Schema(title = "Описание шага")
    String description;
    @Schema(title = "Название шага", required = true)
    String title;
}
