package io.github.ddmfuhrmann.queries.row;

import io.github.ddmfuhrmann.queries.annotation.QueriesColumn;
import io.github.ddmfuhrmann.queries.annotation.QueriesPageable;
import io.github.ddmfuhrmann.queries.annotation.QueriesResource;
import io.github.ddmfuhrmann.queries.page.Order;
import io.github.ddmfuhrmann.queries.page.PageableSpec;

import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves and caches metadata for row record types, including the SQL resource,
 * the mapping between record components and column names, and the optional
 * {@code @QueriesPageable} configuration.
 */
public final class QueriesRowMetadata {

    private static final Map<Class<?>, QueriesRowMetadata> CACHE = new ConcurrentHashMap<>();

    private final String resource;
    private final Map<RecordComponent, String> columns;
    private final Set<String> sortableAliases;
    private final PageableSpec pageableSpec;

    private QueriesRowMetadata(Class<?> rowType) {
        var resourceAnnotation = rowType.getAnnotation(QueriesResource.class);

        if (Objects.isNull(resourceAnnotation)) {
            throw new IllegalStateException("Row type must be annotated with @QueriesResource: %s".formatted(rowType));
        }

        this.resource = resourceAnnotation.value();
        this.columns = Collections.unmodifiableMap(resolveColumns(rowType));
        this.sortableAliases = Set.copyOf(new LinkedHashSet<>(columns.values()));
        this.pageableSpec = resolvePageable(rowType, sortableAliases);
    }

    public static QueriesRowMetadata of(Class<?> rowType) {
        if (!rowType.isRecord()) {
            throw new IllegalArgumentException("Row type must be a record: %s".formatted(rowType));
        }

        return CACHE.computeIfAbsent(rowType, QueriesRowMetadata::new);
    }

    public String resource() {
        return resource;
    }

    public Map<RecordComponent, String> columns() {
        return columns;
    }

    /** Whether the row type is annotated with {@code @QueriesPageable}. */
    public boolean pageable() {
        return pageableSpec != null;
    }

    /** The resolved pageable spec, or {@code null} for a static query. */
    public PageableSpec pageableSpec() {
        return pageableSpec;
    }

    /** Column aliases that may be referenced in an {@code ORDER BY} term. */
    public Set<String> sortableAliases() {
        return sortableAliases;
    }

    private static Map<RecordComponent, String> resolveColumns(Class<?> rowType) {
        Map<RecordComponent, String> map = new LinkedHashMap<>();

        for (RecordComponent component : rowType.getRecordComponents()) {
            QueriesColumn column = component.getAnnotation(QueriesColumn.class);

            String columnName = Objects.requireNonNull(column).value();

            map.put(component, columnName);
        }

        return map;
    }

    private static PageableSpec resolvePageable(Class<?> rowType, Set<String> sortableAliases) {
        QueriesPageable annotation = rowType.getAnnotation(QueriesPageable.class);
        if (annotation == null) {
            return null;
        }

        List<Order> defaultSort = new ArrayList<>();
        for (String field : annotation.defaultSort()) {
            if (!sortableAliases.contains(field)) {
                throw new IllegalStateException(
                        "@QueriesPageable defaultSort references unknown column '%s' on %s. Known columns: %s"
                                .formatted(field, rowType.getName(), sortableAliases));
            }
            defaultSort.add(new Order(field, annotation.defaultOrder()));
        }

        return new PageableSpec(defaultSort, annotation.defaultPageSize());
    }

}
