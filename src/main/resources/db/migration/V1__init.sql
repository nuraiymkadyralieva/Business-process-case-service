-- V1__init.sql
CREATE TABLE IF NOT EXISTS flyway_test (
                                           id BIGSERIAL PRIMARY KEY,
                                           created_at TIMESTAMP NOT NULL DEFAULT NOW()
    );