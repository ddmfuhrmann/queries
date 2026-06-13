package io.github.ddmfuhrmann.queries;

import io.github.ddmfuhrmann.queries.page.PageRequest;

import java.util.List;
import java.util.Optional;

public interface Query<T> {

    /**
     * Runs the query. For a {@code @QueriesPageable} row type the library appends
     * the default sort and page window; for a static row type the SQL runs as-is.
     */
    List<T> list(Object params);

    /**
     * Runs a pageable query with an explicit window/sort. Only valid for row types
     * annotated with {@code @QueriesPageable} — otherwise fails fast.
     */
    List<T> list(Object params, PageRequest page);

    Optional<T> one(Object params);

    /**
     * Total row count for {@code params}, derived from the same SQL resource by
     * wrapping it in {@code SELECT COUNT(*) FROM (...)}. No dedicated count record
     * or {@code *-count.sql} is needed.
     */
    long count(Object params);

}
