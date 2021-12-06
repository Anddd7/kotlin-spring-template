CREATE TABLE IF NOT EXISTS bitwise_operator
(
    id        BIGSERIAL PRIMARY KEY,
    name      VARCHAR(50),
    available INT
);

INSERT INTO bitwise_operator
VALUES (1, '1', 1),
       (2, '2', 2),
       (3, '3', 4),
       (4, '1-2', 3),
       (5, '1-3', 5),
       (6, '2-3', 6),
       (7, '1-2-3', 7);
