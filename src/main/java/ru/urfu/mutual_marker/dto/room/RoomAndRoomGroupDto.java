package ru.urfu.mutual_marker.dto.room;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomAndRoomGroupDto {
    @Schema(title = "Id группы", required = true)
    Long roomGroupId;
    @Schema(title = "Id комнаты", required = true)
    Long roomId;

}
