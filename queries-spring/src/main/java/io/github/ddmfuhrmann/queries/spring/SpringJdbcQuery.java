package io.github.ddmfuhrmann.queries.spring;

import io.github.ddmfuhrmann.queries.Query;
import io.github.ddmfuhrmann.queries.internal.QueriesResourceLoader;
import io.github.ddmfuhrmann.queries.internal.QueriesSqlBuilder;
import io.github.ddmfuhrmann.queries.page.Order;
import io.github.ddmfuhrmann.queries.page.PageRequest;
import io.github.ddmfuhrmann.queries.params.QueriesParamsResolver;
import io.github.ddmfuhrmann.queries.row.QueriesRowMetadata;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

final class SpringJdbcQuery<T> implements Query<T> {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Class<T> rowType;
    private final QueriesRowMetadata metadata;
    private final RowMapper<T> rowMapper;

    SpringJdbcQuery(
            NamedParameterJdbcTemplate jdbcTemplate,
            Class<T> rowType
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowType = rowType;
        this.metadata = QueriesRowMetadata.of(rowType);
        this.rowMapper = new RecordRowMapper<>(rowType, metadata);
    }

    @Override
    public List<T> list(Object params) {
        if (metadata.pageable()) {
            return listPaged(params, metadata.pageableSpec().defaultPage());
        }
        return jdbcTemplate.query(baseSql(), QueriesParamsResolver.resolve(params), rowMapper);
    }

    @Override
    public List<T> list(Object params, PageRequest page) {
        if (!metadata.pageable()) {
            throw new IllegalStateException(
                    "Query is not pageable: %s. Add @QueriesPageable to enable sort/paging.".formatted(rowType.getName()));
        }
        return listPaged(params, page == null ? metadata.pageableSpec().defaultPage() : page);
    }

    @Override
    public Optional<T> one(Object params) {
        List<T> results = jdbcTemplate.query(baseSql(), QueriesParamsResolver.resolve(params), rowMapper);

        if (results.isEmpty()) {
            return Optional.empty();
        }

        if (results.size() > 1) {
            throw new IllegalStateException("Query expected a single result but returned %s".formatted(results.size()));
        }

        return Optional.of(results.getFirst());
    }

    @Override
    public long count(Object params) {
        Long total = jdbcTemplate.queryForObject(
                QueriesSqlBuilder.count(baseSql()),
                QueriesParamsResolver.resolve(params),
                Long.class);
        return total == null ? 0L : total;
    }

    private List<T> listPaged(Object params, PageRequest page) {
        List<Order> orders = page.hasSort() ? page.sort() : metadata.pageableSpec().defaultSort();
        QueriesSqlBuilder.validateSort(orders, metadata.sortableAliases());

        String sql = QueriesSqlBuilder.paginate(baseSql(), orders);

        Map<String, Object> parameters = new HashMap<>(QueriesParamsResolver.resolve(params));
        parameters.put(QueriesSqlBuilder.SIZE_PARAM, page.size());
        parameters.put(QueriesSqlBuilder.OFFSET_PARAM, page.offset());

        return jdbcTemplate.query(sql, parameters, rowMapper);
    }

    private String baseSql() {
        return QueriesResourceLoader.load(metadata.resource());
    }
}
