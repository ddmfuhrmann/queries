package io.github.ddmfuhrmann.queries.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation used to associate a row type with a SQL resource
 * available on the classpath.
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface QueriesResource {

    /**
     * Classpath resource that defines the query.
     */
    String value();
}