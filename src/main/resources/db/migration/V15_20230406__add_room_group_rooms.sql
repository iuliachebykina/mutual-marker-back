DROP TABLE mutual_marker.roomgroup_rooms;


CREATE TABLE mutual_marker.roomgroup_rooms
(
    roomgroup_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    CONSTRAINT pk_roomgroup_rooms PRIMARY KEY (roomgroup_id, room_id)
);
