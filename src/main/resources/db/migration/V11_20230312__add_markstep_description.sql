CREATE TABLE mutual_marker.mark_step_feedback
(
    id         BIGINT NOT NULL,
    owner_id   BIGINT NOT NULL,
    comment  VARCHAR,
    value INTEGER,
    mark_step_id BIGINT NOT NULL,
    deleted    BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT pk_mark_step_feedback PRIMARY KEY (id)
);