CREATE TABLE IF NOT EXISTS pom.file
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ins_time     TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    filename     VARCHAR(40),
    fullpath     VARCHAR(120),
    sender       VARCHAR(20),
    file_comment VARCHAR(100),
    upd_time     TIMESTAMPTZ,
    file_status  VARCHAR(10),
    uli_date     VARCHAR(3)
);