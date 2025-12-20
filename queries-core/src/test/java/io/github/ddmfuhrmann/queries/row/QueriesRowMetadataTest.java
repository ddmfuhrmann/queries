package io.github.ddmfuhrmann.queries.row;

import io.github.ddmfuhrmann.queries.support.TestRecords.MissingColumnAnnotationRow;
import io.github.ddmfuhrmann.queries.support.TestRecords.NoResourceRow;
import io.github.ddmfuhrmann.queries.support.TestRecords.SampleRow;
import org.junit.jupiter.api.Test;

import java.lang.reflect.RecordComponent;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QueriesRowMetadataTest {

    @Test
    void shouldRejectNonRecordTypes() {
        assertThatThrownBy(() -> QueriesRowMetadata.of(String.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Row type must be a record");
    }

    @Test
    void shouldRequireQueriesResourceAnnotation() {
        assertThatThrownBy(() -> QueriesRowMetadata.of(NoResourceRow.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("@QueriesResource");
    }

    @Test
    void shouldFailWhenComponentIsMissingQueriesColumn() {
        assertThatThrownBy(() -> QueriesRowMetadata.of(MissingColumnAnnotationRow.class))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldResolveResourceAndColumnsForValidRecord() {
        QueriesRowMetadata metadata = QueriesRowMetadata.of(SampleRow.class);

        assertThat(metadata.resource()).isEqualTo("sql/sample-row.sql");

        Map<RecordComponent, String> columns = metadata.columns();

        assertThat(columns.values()).containsExactly("id", "name");
        assertThat(columns.keySet())
                .extracting(RecordComponent::getName)
                .containsExactly("id", "name");
    }

    @Test
    void shouldCacheMetadataPerRowType() {
        QueriesRowMetadata first = QueriesRowMetadata.of(SampleRow.class);
        QueriesRowMetadata second = QueriesRowMetadata.of(SampleRow.class);

        assertThat(first).isSameAs(second);
    }
}

