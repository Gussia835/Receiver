CREATE TABLE IF NOT EXISTS pom.unit_error
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    unit_id     BIGINT,
    error_seq   SMALLINT,
    error_code  VARCHAR(3),
    error_field VARCHAR(2000),
    error_msg   VARCHAR(1000),
    file_id     BIGINT,
    CONSTRAINT fk_unit_error_file FOREIGN KEY (file_id) REFERENCES pom.file (id) ON DELETE CASCADE,
    CONSTRAINT fk_unit_error_unit FOREIGN KEY (unit_id) REFERENCES pom.unit (id) ON DELETE SET NULL
);