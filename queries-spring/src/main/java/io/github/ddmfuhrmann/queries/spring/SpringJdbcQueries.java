package io.github.ddmfuhrmann.queries.spring;

import io.github.ddmfuhrmann.queries.Queries;
import io.github.ddmfuhrmann.queries.Query;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public final class SpringJdbcQueries implements Queries {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SpringJdbcQueries(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> Query<T> forType(Class<T> rowType) {
        return new SpringJdbcQuery<>(jdbcTemplate, rowType);
    }

}