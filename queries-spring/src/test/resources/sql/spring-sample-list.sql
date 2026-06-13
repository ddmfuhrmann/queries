SELECT
    id     AS id,
    name   AS name,
    amount AS amount
FROM spring_sample_row
WHERE (:minAmount IS NULL OR amount >= :minAmount)
