package io.github.ddmfuhrmann.queries.page;

import java.util.List;

/**
 * Framework-agnostic page request: the ordering to apply plus the window
 * ({@code size}/{@code offset}). An empty {@code sort} means "fall back to the
 * row record's {@code @QueriesPageable(defaultSort = ...)}".
 */
public record PageRequest(List<Order> sort, int size, long offset) {

    public PageRequest {
        sort = sort == null ? List.of() : List.copyOf(sort);
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be positive, got: " + size);
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Page offset must not be negative, got: " + offset);
        }
    }

    public static PageRequest of(List<Order> sort, int size, long offset) {
        return new PageRequest(sort, size, offset);
    }

    /** Whether an explicit sort was supplied (otherwise the record default applies). */
    public boolean hasSort() {
        return !sort.isEmpty();
    }
}
