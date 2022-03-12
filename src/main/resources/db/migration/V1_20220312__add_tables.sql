CREATE TABLE attachment
(
    id         BIGINT NOT NULL,
    file_name  VARCHAR(255) NOT NULL,
    extension  INTEGER,
    student_id BIGINT NOT NULL,
    deleted    BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT pk_attachment PRIMARY KEY (id)
);

CREATE TABLE mark
(
    id         BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    mark_value INTEGER NOT NULL,
    mark_time  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    comment    VARCHAR(255),
    deleted    BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT pk_mark PRIMARY KEY (id)
);

CREATE TABLE mark_step
(
    id          BIGINT NOT NULL,
    owner_id    BIGINT,
    title       VARCHAR(50) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    max_mark    INTEGER NOT NULL,
    deleted     BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT pk_markstep PRIMARY KEY (id)
);

CREATE TABLE profile
(
    id            BIGINT NOT NULL,
    role          VARCHAR(255) NOT NULL,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL,
    patronymic    VARCHAR(255),
    student_group VARCHAR(255),
    phone_number  VARCHAR(255),
    deleted       BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT pk_profile PRIMARY KEY (id)
);

CREATE TABLE project
(
    id          BIGINT NOT NULL,
    student_id  BIGINT NOT NULL,
    room_id     BIGINT NOT NULL,
    title       VARCHAR(100) NOT NULL,
    description VARCHAR(2000),
    deleted     BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT pk_project PRIMARY KEY (id)
);

CREATE TABLE room
(
    id          BIGINT NOT NULL,
    description VARCHAR(2000) NOT NULL,
    open_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    close_date  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted     BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT pk_room PRIMARY KEY (id)
);

CREATE TABLE MARK_STEP_ROOMS
(
    MARK_STEPS_ID BIGINT NOT NULL,
    ROOMS_ID BIGINT NOT NULL
);

CREATE TABLE PROFILE_ROOMS
(
    TEACHERS_ID BIGINT NOT NULL,
    ROOMS_ID BIGINT NOT NULL
);

CREATE TABLE PROJECT_ATTACHMENTS
(
    PROJECT_ID BIGINT NOT NULL,
    ATTACHMENT_ID BIGINT NOT NULL
);


ALTER TABLE project
    ADD CONSTRAINT FK_PROJECT_ON_ROOM FOREIGN KEY (room_id) REFERENCES room (id);

ALTER TABLE project
    ADD CONSTRAINT FK_PROJECT_ON_STUDENT FOREIGN KEY (student_id) REFERENCES profile (id);

ALTER TABLE mark_step
    ADD CONSTRAINT FK_MARKSTEP_ON_OWNER FOREIGN KEY (owner_id) REFERENCES profile (id);

ALTER TABLE mark
    ADD CONSTRAINT FK_MARK_ON_PROJECT FOREIGN KEY (project_id) REFERENCES project (id);

ALTER TABLE mark
    ADD CONSTRAINT FK_MARK_ON_STUDENT FOREIGN KEY (student_id) REFERENCES profile (id);

ALTER TABLE attachment
    ADD CONSTRAINT FK_ATTACHMENT_ON_STUDENT FOREIGN KEY (student_id) REFERENCES profile (id);