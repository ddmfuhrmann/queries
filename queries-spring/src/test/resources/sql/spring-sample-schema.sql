CREATE TABLE spring_sample_row (
    description VARCHAR(255) NULL,
    active BOOLEAN NULL,
    amount DECIMAL(15,2) NOT NULL,
    birth_date DATE NOT NULL,
    name VARCHAR(100) NOT NULL,
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NULL
);

INSERT INTO spring_sample_row (id, name, birth_date, amount, active, description) VALUES
    ('00000000-0000-0000-0000-000000000003', 'Carol', DATE '2000-12-31', 300.75, FALSE, 'Third'),
    ('00000000-0000-0000-0000-000000000002', 'Bob',   DATE '1985-05-20', 200.00, NULL, NULL),
    ('00000000-0000-0000-0000-000000000001', 'Alice', DATE '1990-01-01', 100.50, TRUE, 'First row');

UPDATE spring_sample_row SET created_at = TIMESTAMP '2020-03-03 10:15:00';