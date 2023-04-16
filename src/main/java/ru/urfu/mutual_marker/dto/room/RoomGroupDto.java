package ru.urfu.mutual_marker.dto.room;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomGroupDto {
    @Schema(title = "Id группы", required = true)
    Long roomGroupId;
    @Schema(title = "Наименование группы комнат", required = true)
    String roomGroupName;
    @Schema(title = "Комнаты", required = true)
    List<RoomDto> rooms;

}
