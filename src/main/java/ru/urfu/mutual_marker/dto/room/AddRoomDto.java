package ru.urfu.mutual_marker.dto.room;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddRoomDto {
    @Schema(title = "Название комнаты", required = true)
    String title;
    @Schema(title = "Id создающего преподавателя", required = true)
    Long teacherId;
    @Schema(title = "Код комнаты")
    String code;
}
