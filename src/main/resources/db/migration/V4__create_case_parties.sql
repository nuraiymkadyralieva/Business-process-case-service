CREATE TABLE case_parties (
                              id BIGSERIAL PRIMARY KEY,
                              case_id BIGINT NOT NULL REFERENCES cases(id) ON DELETE CASCADE,

                              party_type VARCHAR(30) NOT NULL,      -- PERSON / LEGAL_ENTITY
                              role VARCHAR(80) NOT NULL,            -- свободная роль (пока без справочника)

                              external_ref VARCHAR(120) NOT NULL,   -- ссылка на сущность (id/inn/snils/uuid/...)
                              display_name VARCHAR(200),            -- опционально, для удобства отображения

                              created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_case_parties_case_id ON case_parties(case_id);
CREATE INDEX idx_case_parties_case_id_role ON case_parties(case_id, role);