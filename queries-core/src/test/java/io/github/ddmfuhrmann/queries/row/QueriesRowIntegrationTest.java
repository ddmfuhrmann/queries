package io.github.ddmfuhrmann.queries.row;

import io.github.ddmfuhrmann.queries.internal.QueriesResourceLoader;
import io.github.ddmfuhrmann.queries.support.TestRecords.SampleRow;
import org.junit.jupiter.api.Test;

import java.lang.reflect.RecordComponent;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class QueriesRowIntegrationTest {

    @Test
    void shouldResolveResourceAndColumnsAndLoadSql() {
        QueriesRowMetadata metadata = QueriesRowMetadata.of(SampleRow.class);

        assertThat(metadata.resource()).isEqualTo("sql/sample-row.sql");

        String sql = QueriesResourceLoader.load(metadata.resource());

        assertThat(sql).contains("select 1 as id, 'sample' as name");

        Map<RecordComponent, String> columns = metadata.columns();
        RecordComponent[] components = SampleRow.class.getRecordComponents();

        assertThat(columns).hasSize(components.length);
        assertThat(columns.values()).containsExactly("id", "name");
    }
}

