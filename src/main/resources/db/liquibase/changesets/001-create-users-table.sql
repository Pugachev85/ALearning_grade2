--liquibase formatted sql
--changeset author:admin:001

CREATE TABLE IF NOT EXISTS users (
 id BIGSERIAL PRIMARY KEY,
 name VARCHAR(100) NOT NULL,
 email VARCHAR(100) NOT NULL UNIQUE,
 age INTEGER NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
