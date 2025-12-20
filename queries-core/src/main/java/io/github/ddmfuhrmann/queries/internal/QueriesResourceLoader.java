package io.github.ddmfuhrmann.queries.internal;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Utility class responsible for loading SQL resources from the classpath.
 */
public class QueriesResourceLoader {

    private QueriesResourceLoader() {}

    public static String load(String resource) {
        if (resource == null || resource.isBlank()) {
            throw new IllegalArgumentException("SQL resource name must not be null or blank");
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = QueriesResourceLoader.class.getClassLoader();
        }

        try (InputStream stream = classLoader.getResourceAsStream(resource)) {
            if (stream == null) {
                throw new IllegalStateException("SQL resource not found: %s".formatted(resource));
            }

            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load SQL resource: %s".formatted(resource), e);
        }
    }

}