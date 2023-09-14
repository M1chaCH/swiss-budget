CREATE TABLE parent
(
    id   serial primary key,
    name varchar(250) not null
);

CREATE TABLE child
(
    id        serial primary key,
    parent_id int          not null,
    name      varchar(250) not null,
    age       int,
    FOREIGN KEY (parent_id) REFERENCES parent (id) ON DELETE CASCADE
);

INSERT INTO parent (name)
VALUES ('test parent');

INSERT INTO child (parent_id, name, age)
VALUES (1, 'test child', 12),
       (1, 'second child', 8);