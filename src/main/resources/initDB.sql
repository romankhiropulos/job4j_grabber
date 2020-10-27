DROP TABLE IF EXISTS post;

CREATE TABLE post
(
    id      CHAR(36) PRIMARY KEY,
    name    TEXT        NOT NULL,
    text    TEXT        NOT NULL,
    link    TEXT UNIQUE NOT NULL,
    created TIMESTAMP   NOT NULL
);