package io.github.ddmfuhrmann.queries.params;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Resolves named parameters from a record instance into a map,
 * using the record component names as parameter names.
 */
public final class QueriesParamsResolver {

    private QueriesParamsResolver() {}

    /**
     * Resolves the given record instance into a map of parameter names to values.
     *
     * @param params record instance containing query parameters; may be {@code null}
     * @return immutable empty map if {@code params} is {@code null},
     *         otherwise a mutable map with one entry per record component
     */
    public static Map<String, Object> resolve(Object params) {
        if (params == null) {
            return Map.of();
        }

        Class<?> type = params.getClass();
        if (!type.isRecord()) {
            throw new IllegalArgumentException("Query params must be a record. Got: %s".formatted(type.getName()));
        }

        Map<String, Object> values = new HashMap<>();
        for (RecordComponent component : type.getRecordComponents()) {
            Object value = readComponentValue(component, params);
            values.put(component.getName(), value);
        }

        return Collections.unmodifiableMap(values);
    }

    private static Object readComponentValue(RecordComponent component, Object instance) {
        try {
            return component.getAccessor().invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to read record component: %s".formatted(component.getName()), e);
        }
    }
}