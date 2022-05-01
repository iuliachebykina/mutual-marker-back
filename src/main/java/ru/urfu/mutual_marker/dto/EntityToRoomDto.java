package ru.urfu.mutual_marker.dto;

import lombok.Data;
import ru.urfu.mutual_marker.jpa.entity.Room;

@Data
public class EntityToRoomDto {
    Object entity;
    Room room;
}
