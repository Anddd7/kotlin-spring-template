CREATE TABLE IF NOT EXISTS user_info
(
    id      BIGSERIAL PRIMARY KEY,
    email   VARCHAR(50),
    user_id BIGINT
);

CREATE TABLE IF NOT EXISTS employee
(
    id         BIGSERIAL PRIMARY KEY,
    department VARCHAR(50),
    user_id    BIGINT
);
