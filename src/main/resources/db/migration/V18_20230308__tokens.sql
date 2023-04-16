CREATE TABLE mutual_marker.token
(
    login      varchar unique ,
    token      varchar,
    deleted    BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT pk_token PRIMARY KEY (login)
);
