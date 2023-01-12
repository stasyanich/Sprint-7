--liquibase formatted sql

--changeset set:01

INSERT INTO account1 (id, amount, version)
VALUES (1, 1000, 0), (2, 2000, 0)