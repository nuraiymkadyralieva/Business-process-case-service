CREATE TABLE status_history (
                                id BIGSERIAL PRIMARY KEY,
                                case_id BIGINT NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
                                previous_status VARCHAR(50) NOT NULL,
                                new_status VARCHAR(50) NOT NULL,
                                changed_at TIMESTAMP NOT NULL,
                                initiated_by VARCHAR(100)
);

CREATE INDEX idx_status_history_case_id ON status_history(case_id);