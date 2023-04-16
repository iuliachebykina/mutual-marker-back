package ru.urfu.mutual_marker.dto.task;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.util.Set;

@Value
public class MarkStep {
    @Schema(title = "ИД шага оценивания", example = "1")
    Long id;

    @Schema(title = "Заголовок шага", example = "Наличие файла")
    String title;

    @Schema(title = "Описание шага", description = "Что должно быть в работе", example = "Файл загружен")
    String description;

    @Schema(title = "Градация оценки", description = "Как можно оценить")
    Set<Integer> values;

    @Schema(title = "Статус жизни")
    Boolean deleted;
}
