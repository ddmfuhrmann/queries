SELECT
    id,
    name,
    birth_date AS birth_date_legacy,
    created_at AS created_at_legacy,
    amount,
    active,
    description
FROM spring_sample_row
WHERE id = :id;
