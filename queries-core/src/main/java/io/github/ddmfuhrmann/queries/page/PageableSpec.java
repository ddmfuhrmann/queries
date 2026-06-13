package io.github.ddmfuhrmann.queries.page;

import java.util.List;

/**
 * Resolved {@code @QueriesPageable} configuration for a row type: the default
 * ordering and page size to apply when the caller supplies none.
 */
public record PageableSpec(List<Order> defaultSort, int defaultPageSize) {

    public PageableSpec {
        defaultSort = defaultSort == null ? List.of() : List.copyOf(defaultSort);
        if (defaultPageSize <= 0) {
            throw new IllegalArgumentException("defaultPageSize must be positive, got: " + defaultPageSize);
        }
    }

    /** A {@link PageRequest} using this spec's defaults, starting at offset 0. */
    public PageRequest defaultPage() {
        return PageRequest.of(defaultSort, defaultPageSize, 0L);
    }
}
