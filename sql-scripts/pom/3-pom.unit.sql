CREATE TABLE IF NOT EXISTS pom.unit
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    file_id    BIGINT,
    ins_time   TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    pom_type   VARCHAR(3),
    status     VARCHAR(10),
    unit_value VARCHAR(2000),
    upd_time   TIMESTAMPTZ,
    add_value  VARCHAR(100),
    CONSTRAINT fk_unit_file FOREIGN KEY (file_id) REFERENCES pom.file (id) ON DELETE CASCADE
);