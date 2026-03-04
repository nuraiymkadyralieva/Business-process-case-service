CREATE TABLE party_roles (
                             id BIGSERIAL PRIMARY KEY,
                             code VARCHAR(50) NOT NULL UNIQUE,
                             name VARCHAR(150) NOT NULL,
                             description TEXT
);

INSERT INTO party_roles (code, name, description) VALUES
                                                      ('DEBTOR', 'Debtor', 'Party owing debt'),
                                                      ('CREDITOR', 'Creditor', 'Party claiming debt'),
                                                      ('ARBITRATION_MANAGER', 'Arbitration Manager', 'Process manager'),
                                                      ('COURT', 'Court', 'Court authority'),
                                                      ('OTHER', 'Other', 'Other participant');