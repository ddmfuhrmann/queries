package io.github.ddmfuhrmann.queries.page;

import java.util.Objects;

/**
 * A single ordering term: a {@code field} (which must match a {@code @QueriesColumn}
 * alias on the row record) and a {@link Direction}.
 */
public record Order(String field, Direction direction) {

    public Order {
        if (field == null || field.isBlank()) {
            throw new IllegalArgumentException("Order field must not be null or blank");
        }
        Objects.requireNonNull(direction, "Order direction must not be null");
    }

    public static Order asc(String field) {
        return new Order(field, Direction.ASC);
    }

    public static Order desc(String field) {
        return new Order(field, Direction.DESC);
    }
}
