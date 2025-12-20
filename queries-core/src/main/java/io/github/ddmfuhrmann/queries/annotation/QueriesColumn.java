package io.github.ddmfuhrmann.queries.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation used to map a record component to a column alias
 * in the SQL query result.
 */
@Retention(RUNTIME)
@Target(RECORD_COMPONENT)
public @interface QueriesColumn {

    /**
     * Column alias in the query result.
     */
    String value();
}