package io.github.ddmfuhrmann.queries.row;

import io.github.ddmfuhrmann.queries.support.TestRecords.SampleRow;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QueriesRecordConstructorTest {

    @Test
    void shouldResolveCanonicalConstructorForRecord() throws Exception {
        var constructor = QueriesRecordConstructor.resolve(SampleRow.class);

        assertThat(constructor).isNotNull();
        assertThat(constructor.canAccess(null)).isTrue();

        var instance = constructor.newInstance(1L, "sample");

        assertThat(instance.id()).isEqualTo(1L);
        assertThat(instance.name()).isEqualTo("sample");
    }

    @Test
    void shouldRejectNullType() {
        assertThatThrownBy(() -> QueriesRecordConstructor.resolve(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("recordType must not be null");
    }

    @Test
    void shouldRejectNonRecordType() {
        assertThatThrownBy(() -> QueriesRecordConstructor.resolve(String.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Type must be a record");
    }

}