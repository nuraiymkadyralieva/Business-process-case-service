CREATE TABLE case_parties (
                              id BIGSERIAL PRIMARY KEY,
                              case_id BIGINT NOT NULL,
                              party_type VARCHAR(30) NOT NULL,
                              party_role_id BIGINT NOT NULL,
                              external_ref VARCHAR(120) NOT NULL,
                              display_name VARCHAR(200),
                              created_at TIMESTAMP NOT NULL DEFAULT NOW(),

                              CONSTRAINT fk_case_parties_case
                                  FOREIGN KEY (case_id)
                                      REFERENCES cases(id),

                              CONSTRAINT fk_case_parties_role
                                  FOREIGN KEY (party_role_id)
                                      REFERENCES party_roles(id)
);