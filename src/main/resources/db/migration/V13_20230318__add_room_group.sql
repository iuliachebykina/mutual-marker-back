alter table room
    add room_group_id BIGINT;

CREATE TABLE mutual_marker.room_group
(
    id          BIGINT NOT NULL,
    title       VARCHAR(100),
    profile_id BIGINT NOT NULL,
    deleted     BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT pk_room_group PRIMARY KEY (id)
);

ALTER TABLE mutual_marker.room_group
    ADD CONSTRAINT FK_ROOM_GROUP_ON_PROFILE FOREIGN KEY (profile_id) REFERENCES profile (id);



