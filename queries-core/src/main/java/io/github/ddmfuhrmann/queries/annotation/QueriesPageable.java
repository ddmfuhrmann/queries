package io.github.ddmfuhrmann.queries.annotation;

import io.github.ddmfuhrmann.queries.page.Direction;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks a row record as <em>pageable</em>: the library owns the query tail and
 * appends {@code ORDER BY ... LIMIT ... OFFSET ...} on {@code list}, deriving the
 * count by wrapping the base query.
 * <p>
 * The associated {@code .sql} resource must therefore be the <strong>base query
 * only</strong> — no {@code ORDER BY}, {@code LIMIT} or {@code OFFSET}. A row record
 * <em>without</em> this annotation is static (self-contained SQL); passing a
 * {@code PageRequest} to such a query fails fast.
 *
 * <p>{@link #defaultSort()} entries reference {@code @QueriesColumn} aliases on the
 * same record and double as the allow-list for any caller-supplied sort.
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface QueriesPageable {

    /**
     * Default ordering columns, applied when the caller supplies no sort.
     * Order matters — list the unique tiebreaker (e.g. {@code id}) last so
     * offset pagination stays stable. Each entry must be a {@code @QueriesColumn}
     * alias on this record.
     */
    String[] defaultSort() default {};

    /** Direction applied to every {@link #defaultSort()} column. */
    Direction defaultOrder() default Direction.ASC;

    /** Page size used when the caller supplies no {@code PageRequest}. */
    int defaultPageSize() default 100;
}
