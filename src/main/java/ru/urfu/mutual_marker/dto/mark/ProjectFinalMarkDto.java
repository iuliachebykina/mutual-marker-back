package ru.urfu.mutual_marker.dto.mark;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.urfu.mutual_marker.jpa.entity.value_type.Name;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class ProjectFinalMarkDto {
    @Schema(name = "Имя студента")
    Name studentName;
    @Schema(name = "Название проекта")
    String projectTitle;
    @Schema(name = "Id проекта")
    Long projectId;
    @Schema(name = "Id профиля студента, которому принадлежит оценка")
    Long profileId;
    @Schema(name = "Финальная оценка с точностью до 2 цифр после запятой, рассчитывается повторно при повторных запросах")
    Double finalMark;
    @Schema(name = "Группа студента")
    String group;
}
