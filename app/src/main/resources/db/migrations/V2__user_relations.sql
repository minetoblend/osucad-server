CREATE TABLE IF NOT EXISTS user_relations
(
    from_id INT,
    to_id   INT,
    kind    INT NOT NULL,
    CONSTRAINT pk_user_relations PRIMARY KEY (from_id, to_id),
    CONSTRAINT fk_user_relations_from_id__id FOREIGN KEY (from_id) REFERENCES users (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_user_relations_to_id__id FOREIGN KEY (to_id) REFERENCES users (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);
