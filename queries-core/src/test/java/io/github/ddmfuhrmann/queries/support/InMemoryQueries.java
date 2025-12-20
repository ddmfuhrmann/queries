package io.github.ddmfuhrmann.queries.support;

import io.github.ddmfuhrmann.queries.Queries;
import io.github.ddmfuhrmann.queries.Query;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryQueries implements Queries {

    private final Map<Class<?>, List<?>> store;

    public InMemoryQueries(Map<Class<?>, List<?>> store) {
        this.store = Map.copyOf(store);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Query<T> forType(Class<T> rowType) {
        List<?> rows = store.getOrDefault(rowType, List.of());
        return new InMemoryQuery<>((List<T>) rows);
    }

    private record InMemoryQuery<T>(List<T> rows) implements Query<T> {

            private InMemoryQuery(List<T> rows) {
                this.rows = List.copyOf(rows);
            }

            @Override
            public List<T> list(Object params) {
                return Collections.unmodifiableList(rows);
            }

            @Override
            public Optional<T> one(Object params) {
                if (rows.isEmpty()) {
                    return Optional.empty();
                }
                return Optional.of(rows.getFirst());
            }
        }
}

