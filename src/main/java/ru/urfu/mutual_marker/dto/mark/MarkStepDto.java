package ru.urfu.mutual_marker.dto.mark;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarkStepDto {
    @Schema(title = "Значения критерия", required = true)
    List<Integer> values;
    @Schema(title = "Описание критерия", required = true)
    String description;
    @Schema(title = "Критерий", required = true)
    String title;
}
