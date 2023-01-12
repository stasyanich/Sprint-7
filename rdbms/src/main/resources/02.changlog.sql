--liquibase formatted sql

--changeset set:02

CREATE INDEX account1_version_idx ON account1 ("version", id)