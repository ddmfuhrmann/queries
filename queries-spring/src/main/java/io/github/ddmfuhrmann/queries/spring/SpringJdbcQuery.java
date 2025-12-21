package io.github.ddmfuhrmann.queries.spring;

import io.github.ddmfuhrmann.queries.Query;
import io.github.ddmfuhrmann.queries.internal.QueriesResourceLoader;
import io.github.ddmfuhrmann.queries.params.QueriesParamsResolver;
import io.github.ddmfuhrmann.queries.row.QueriesRowMetadata;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Optional;

final class SpringJdbcQuery<T> implements Query<T> {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final QueriesRowMetadata metadata;
    private final RowMapper<T> rowMapper;

    SpringJdbcQuery(
            NamedParameterJdbcTemplate jdbcTemplate,
            Class<T> rowType
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.metadata = QueriesRowMetadata.of(rowType);
        this.rowMapper = new RecordRowMapper<>(rowType, metadata);
    }

    @Override
    public List<T> list(Object params) {
        var sql = QueriesResourceLoader.load(metadata.resource());
        var parameters = QueriesParamsResolver.resolve(params);

        return jdbcTemplate.query(sql, parameters, rowMapper);
    }

    @Override
    public Optional<T> one(Object params) {
        List<T> results = list(params);

        if (results.isEmpty()) {
            return Optional.empty();
        }

        if (results.size() > 1) {
            throw new IllegalStateException("Query expected a single result but returned %s".formatted(results.size()));
        }

        return Optional.of(results.getFirst());
    }

}