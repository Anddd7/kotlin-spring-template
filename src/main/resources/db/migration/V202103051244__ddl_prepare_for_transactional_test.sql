CREATE TABLE transaction_hub
(
    id  BIGSERIAL PRIMARY KEY,
    id1 BIGINT,
    id2 BIGINT,
    id3 BIGINT
);

CREATE TABLE transaction_part1
(
    id   BIGSERIAL PRIMARY KEY,
    name varchar(50)
);
CREATE TABLE transaction_part2
(
    id   BIGSERIAL PRIMARY KEY,
    name varchar(50)
);
CREATE TABLE transaction_part3
(
    id   BIGSERIAL PRIMARY KEY,
    name varchar(50)
);