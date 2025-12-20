package io.github.ddmfuhrmann.queries.params;

import io.github.ddmfuhrmann.queries.support.TestRecords.NullableParams;
import io.github.ddmfuhrmann.queries.support.TestRecords.SimpleParams;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QueriesParamsResolverTest {

    @Test
    void shouldReturnEmptyMapWhenParamsIsNull() {
        Map<String, Object> result = QueriesParamsResolver.resolve(null);

        assertThat(result).isEmpty();
        assertThatThrownBy(() -> result.put("x", 1)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldRejectNonRecordParams() {
        assertThatThrownBy(() -> QueriesParamsResolver.resolve(new Object()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Query params must be a record");
    }

    @Test
    void shouldResolveSimpleRecordParams() {
        SimpleParams params = new SimpleParams("Alice", 30);

        Map<String, Object> result = QueriesParamsResolver.resolve(params);

        assertThat(result)
                .containsEntry("name", "Alice")
                .containsEntry("age", 30);

        assertThatThrownBy(() -> result.put("x", 1)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldAllowNullValues() {
        NullableParams params = new NullableParams(null, null);

        Map<String, Object> result = QueriesParamsResolver.resolve(params);

        assertThat(result)
                .containsEntry("name", null)
                .containsEntry("description", null);
    }
}

