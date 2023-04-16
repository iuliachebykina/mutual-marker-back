package ru.urfu.mutual_marker.dto.mark;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.urfu.mutual_marker.dto.task.TaskInfo;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbacksForTaskDto {
    @Schema(title = "Информация по заданию")
    TaskInfo taskInfo;

    @Schema(title = "Отзывы")
    List<MarkFeedbackDto> feedbacks;
}
