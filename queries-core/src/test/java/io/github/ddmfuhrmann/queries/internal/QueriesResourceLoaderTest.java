package io.github.ddmfuhrmann.queries.internal;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QueriesResourceLoaderTest {

    @Test
    void shouldRejectNullOrBlankResourceName() {
        assertThatThrownBy(() -> QueriesResourceLoader.load(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be null or blank");

        assertThatThrownBy(() -> QueriesResourceLoader.load(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be null or blank");

        assertThatThrownBy(() -> QueriesResourceLoader.load("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be null or blank");
    }

    @Test
    void shouldFailWhenResourceDoesNotExist() {
        assertThatThrownBy(() -> QueriesResourceLoader.load("sql/does-not-exist.sql"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to load SQL resource");
    }

    @Test
    void shouldLoadExistingSqlResource() {
        String sql = QueriesResourceLoader.load("sql/sample-row.sql");

        assertThat(sql).contains("select 1 as id, 'sample' as name");
    }

    @Test
    void shouldUseClassLoaderFromClassWhenContextClassLoaderIsNull() {
        ClassLoader original = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(null);

            assertThatThrownBy(() -> QueriesResourceLoader.load("sql/does-not-exist.sql"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Failed to load SQL resource");
        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }
}

