CREATE TABLE cases (
                       id BIGSERIAL PRIMARY KEY,
                       case_number VARCHAR(100) NOT NULL UNIQUE,
                       procedure_type VARCHAR(50) NOT NULL,
                       status VARCHAR(50) NOT NULL,
                       start_date TIMESTAMP NOT NULL,
                       end_date TIMESTAMP
);