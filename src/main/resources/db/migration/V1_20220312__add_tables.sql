create schema if not exists mutual_marker;

CREATE TABLE mutual_marker.attachment
(
    id         BIGINT NOT NULL,
    file_name  VARCHAR(255) NOT NULL,
    extension  INTEGER,
    student_id BIGINT NOT NULL,
    deleted    BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT pk_attachment PRIMARY KEY (id)
);

CREATE TABLE mutual_marker.mark
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

CREATE TABLE mutual_marker.mark_step
(
    id          BIGINT NOT NULL,
    owner_id    BIGINT,
    title       VARCHAR(100) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    max_mark    INTEGER NOT NULL,
    deleted     BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT pk_markstep PRIMARY KEY (id)
);

CREATE TABLE mutual_marker.profile
(
    id            BIGINT NOT NULL,
    password      varchar(255) NOT NULL,
    email         varchar(255) NOT NULL,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    role          VARCHAR(30) NOT NULL,
    patronymic    VARCHAR(100),
    university    VARCHAR(300),
    institute     VARCHAR(300),
    subject       varchar(300),
    student_group VARCHAR(100),
    phone_number  VARCHAR(100),
    social_network varchar(300),
    deleted       BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT pk_profile PRIMARY KEY (id)
);

CREATE TABLE mutual_marker.project
(
    id          BIGINT NOT NULL,
    student_id  BIGINT NOT NULL,
    task_id     BIGINT NOT NULL,
    title       VARCHAR(100) NOT NULL,
    description VARCHAR(2000),
    deleted     BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT pk_project PRIMARY KEY (id)
);

CREATE TABLE mutual_marker.room
(
    id          BIGINT NOT NULL,
    title       VARCHAR(100),
    deleted     BOOLEAN DEFAULT FALSE NOT NULL,
    code        VARCHAR(50) NOT NULL,
    CONSTRAINT pk_room PRIMARY KEY (id)
);

CREATE TABLE mutual_marker.task
(
    id          BIGINT NOT NULL,
    title       VARCHAR(100)  not null ,
    description varchar(2000),
    open_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    close_date  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    room_id     BIGINT not null,
    deleted     BOOLEAN DEFAULT FALSE NOT NULL,
    min_graded  INTEGER DEFAULT 0 NOT NULL,
    CONSTRAINT pk_task PRIMARY KEY (id)
);


CREATE TABLE mutual_marker.markstep_tasks
(
    MARK_STEP_ID BIGINT NOT NULL,
    TASK_ID BIGINT NOT NULL
);

CREATE TABLE mutual_marker.profile_rooms
(
    PROFILE_ID BIGINT NOT NULL,
    ROOM_ID BIGINT NOT NULL
);

CREATE TABLE mutual_marker.project_attachments
(
    PROJECT_ID BIGINT NOT NULL,
    ATTACHMENT_ID BIGINT NOT NULL
);

CREATE TABLE mutual_marker.mark_step_value
(
     id BIGINT NOT NULL,
     value BIGINT NOT NULL,
     mark_step_id BIGINT NOT NULL,
     deleted BOOLEAN DEFAULT FALSE NOT NULL,
     CONSTRAINT pk_value PRIMARY KEY (id)
);

CREATE TABLE mutual_marker.number_of_graded
(
    id BIGINT NOT NULL,
    task_id BIGINT NOT NULL,
    profile_id BIGINT NOT NULL,
    graded INTEGER DEFAULT 0 NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT pk_graded PRIMARY KEY (id)
);


ALTER TABLE mutual_marker.project
    ADD CONSTRAINT FK_PROJECT_ON_ROOM FOREIGN KEY (task_id) REFERENCES task (id);

ALTER TABLE mutual_marker.project
    ADD CONSTRAINT FK_PROJECT_ON_STUDENT FOREIGN KEY (student_id) REFERENCES profile (id);

ALTER TABLE mutual_marker.mark_step
    ADD CONSTRAINT FK_MARKSTEP_ON_OWNER FOREIGN KEY (owner_id) REFERENCES profile (id);

ALTER TABLE mutual_marker.mark
    ADD CONSTRAINT FK_MARK_ON_PROJECT FOREIGN KEY (project_id) REFERENCES project (id);

ALTER TABLE mutual_marker.mark
    ADD CONSTRAINT FK_MARK_ON_STUDENT FOREIGN KEY (student_id) REFERENCES profile (id);

ALTER TABLE mutual_marker.attachment
    ADD CONSTRAINT FK_ATTACHMENT_ON_STUDENT FOREIGN KEY (student_id) REFERENCES profile (id);

ALTER TABLE mutual_marker.mark_step_value
    ADD CONSTRAINT FK_VALUE_ON_STEP FOREIGN KEY (mark_step_id) REFERENCES mark_step (id);

ALTER TABLE mutual_marker.number_of_graded
    ADD CONSTRAINT FK_GRADED_ON_STUDENT FOREIGN KEY (profile_id) REFERENCES profile (id);

ALTER TABLE mutual_marker.number_of_graded
    ADD CONSTRAINT FK_GRADED_ON_TASK FOREIGN KEY (task_id) REFERENCES task (id);