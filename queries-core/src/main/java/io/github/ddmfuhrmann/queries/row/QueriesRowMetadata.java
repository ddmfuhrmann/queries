package io.github.ddmfuhrmann.queries.row;

import io.github.ddmfuhrmann.queries.annotation.QueriesColumn;
import io.github.ddmfuhrmann.queries.annotation.QueriesResource;

import java.lang.reflect.RecordComponent;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves and caches metadata for row record types, including the SQL resource
 * and the mapping between record components and column names.
 */
public final class QueriesRowMetadata {

    private static final Map<Class<?>, QueriesRowMetadata> CACHE = new ConcurrentHashMap<>();

    private final String resource;
    private final Map<RecordComponent, String> columns;

    private QueriesRowMetadata(Class<?> rowType) {
        var resourceAnnotation = rowType.getAnnotation(QueriesResource.class);

        if (Objects.isNull(resourceAnnotation)) {
            throw new IllegalStateException("Row type must be annotated with @QueriesResource: %s".formatted(rowType));
        }

        this.resource = resourceAnnotation.value();
        this.columns = Collections.unmodifiableMap(resolveColumns(rowType));
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

    private static Map<RecordComponent, String> resolveColumns(Class<?> rowType) {
        Map<RecordComponent, String> map = new LinkedHashMap<>();

        for (RecordComponent component : rowType.getRecordComponents()) {
            QueriesColumn column = component.getAnnotation(QueriesColumn.class);

            String columnName = Objects.requireNonNull(column).value();

            map.put(component, columnName);
        }

        return map;
    }

}