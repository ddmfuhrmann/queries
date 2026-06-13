package io.github.ddmfuhrmann.queries.internal;

import io.github.ddmfuhrmann.queries.page.Order;

import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Pattern;

/**
 * Builds the dynamic tail (sort + page window) and the derived count query from a
 * base SQL resource. Kept framework-agnostic so any {@code Queries} implementation
 * can reuse it.
 */
public final class QueriesSqlBuilder {

    /** Named parameters the library injects for the page window. */
    public static final String SIZE_PARAM = "__size";
    public static final String OFFSET_PARAM = "__offset";

    /** Trailing {@code LIMIT ...} clause not crossing a closing paren (i.e. not a subquery's). */
    private static final Pattern TRAILING_LIMIT = Pattern.compile("(?is)\\s+limit\\s+[^)]*$");

    private QueriesSqlBuilder() {}

    /**
     * Appends {@code ORDER BY <orders> LIMIT :__size OFFSET :__offset} to {@code baseSql}.
     * {@code orders} must be non-empty and already validated.
     */
    public static String paginate(String baseSql, List<Order> orders) {
        if (orders.isEmpty()) {
            throw new IllegalArgumentException("A pageable query requires at least one sort column");
        }
        return stripTrailing(baseSql)
                + " ORDER BY " + renderOrderBy(orders)
                + " LIMIT :" + SIZE_PARAM + " OFFSET :" + OFFSET_PARAM;
    }

    /** Wraps {@code baseSql} (minus any trailing {@code LIMIT}) in a {@code COUNT(*)}. */
    public static String count(String baseSql) {
        return "SELECT COUNT(*) FROM (" + stripTrailing(baseSql) + ") _c";
    }

    /** Fails fast if any order references a column outside the allow-list. */
    public static void validateSort(List<Order> orders, Set<String> allowed) {
        for (Order order : orders) {
            if (!allowed.contains(order.field())) {
                throw new IllegalArgumentException(
                        "Unknown sort column '%s'. Allowed: %s".formatted(order.field(), allowed));
            }
        }
    }

    private static String renderOrderBy(List<Order> orders) {
        StringJoiner joiner = new StringJoiner(", ");
        for (Order order : orders) {
            joiner.add(order.field() + " " + order.direction().name());
        }
        return joiner.toString();
    }

    private static String stripTrailing(String sql) {
        String trimmed = sql.strip();
        if (trimmed.endsWith(";")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return TRAILING_LIMIT.matcher(trimmed).replaceAll("").strip();
    }
}
