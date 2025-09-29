CREATE TABLE IF NOT EXISTS users
(
    id              SERIAL PRIMARY KEY,
    username        VARCHAR(255) NOT NULL,
    last_login_time TIMESTAMP    NULL
);
CREATE TABLE IF NOT EXISTS beatmapsets
(
    id         SERIAL PRIMARY KEY,
    creator_id INT                                 NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    artist     VARCHAR(255)                        NOT NULL,
    title      VARCHAR(255)                        NOT NULL,
    CONSTRAINT fk_beatmapsets_creator_id__id FOREIGN KEY (creator_id) REFERENCES users (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);
CREATE TABLE IF NOT EXISTS documents
(
    id SERIAL PRIMARY KEY
);
CREATE TABLE IF NOT EXISTS beatmaps
(
    id                  SERIAL PRIMARY KEY,
    beatmapset_id       INT                                 NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    difficulty_owner_id INT                                 NULL,
    artist              VARCHAR(255)                        NOT NULL,
    title               VARCHAR(255)                        NOT NULL,
    difficulty_name     VARCHAR(255)                        NOT NULL,
    document_id         INT                                 NOT NULL,
    CONSTRAINT fk_beatmaps_beatmapset_id__id FOREIGN KEY (beatmapset_id) REFERENCES beatmapsets (id) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT fk_beatmaps_difficulty_owner_id__id FOREIGN KEY (difficulty_owner_id) REFERENCES users (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_beatmaps_document_id__id FOREIGN KEY (document_id) REFERENCES documents (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);
CREATE TABLE IF NOT EXISTS blobs
(
    id     uuid PRIMARY KEY,
    "size" INT NOT NULL
);
CREATE TABLE IF NOT EXISTS document_snapshots
(
    document_id     INT,
    sequence_number BIGINT,
    summary_blob_id uuid NOT NULL,
    CONSTRAINT pk_document_snapshots PRIMARY KEY (document_id, sequence_number),
    CONSTRAINT fk_document_snapshots_document_id__id FOREIGN KEY (document_id) REFERENCES documents (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_document_snapshots_summary_blob_id__id FOREIGN KEY (summary_blob_id) REFERENCES blobs (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);
CREATE TABLE IF NOT EXISTS document_blob_usages
(
    document_id INT,
    blob_id     uuid,
    CONSTRAINT pk_document_blob_usages PRIMARY KEY (document_id, blob_id),
    CONSTRAINT fk_document_blob_usages_document_id__id FOREIGN KEY (document_id) REFERENCES documents (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_document_blob_usages_blob_id__id FOREIGN KEY (blob_id) REFERENCES blobs (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);
CREATE SEQUENCE IF NOT EXISTS users_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS beatmapsets_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS beatmaps_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS documents_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
