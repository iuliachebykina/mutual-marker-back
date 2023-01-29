package ru.urfu.mutual_marker.dto.mark;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.urfu.mutual_marker.dto.mark.AddMarkDto;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddTeacherMarkDto extends AddMarkDto {
    @Schema(title = "Коэффициент оценки преподавателя", minimum = "0", maximum = "1", required = true)
    Double coefficient;
}
