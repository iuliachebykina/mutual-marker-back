package ru.urfu.mutual_marker.dto;

import lombok.Data;
import ru.urfu.mutual_marker.jpa.entity.Room;

@Data
public class AddEntityToRoomDto {
    Long entityId;
    String roomCode;
}
