CREATE TABLE IF NOT EXISTS filehashes
(
    jobid VARCHAR(255) PRIMARY KEY,
    hash  BYTEA NOT NULL
);
CREATE EXTENSION IF NOT EXISTS tablefunc;