package ru.urfu.mutual_marker.dto.room;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.urfu.mutual_marker.jpa.entity.Room;

@Data
public class AddEntityToRoomDto {
    @Schema(title = "Id добавляемой сущности: задачи/профиля", required = true)
    Long entityId;
    @Schema(title = "Код комнаты для добавления", required = true)
    String roomCode;
}
