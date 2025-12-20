package io.github.ddmfuhrmann.queries.support;

import io.github.ddmfuhrmann.queries.Query;
import io.github.ddmfuhrmann.queries.support.TestRecords.SampleRow;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryQueriesTest {

    @Test
    void shouldReturnEmptyQueryForUnregisteredType() {
        InMemoryQueries queries = new InMemoryQueries(Map.of());

        Query<SampleRow> query = queries.forType(SampleRow.class);

        assertThat(query.list(null)).isEmpty();
        assertThat(query.one(null)).isEmpty();
    }

    @Test
    void shouldReturnAllRowsFromList() {
        SampleRow row1 = new SampleRow(1L, "one");
        SampleRow row2 = new SampleRow(2L, "two");

        InMemoryQueries queries = new InMemoryQueries(Map.of(SampleRow.class, List.of(row1, row2)));

        Query<SampleRow> query = queries.forType(SampleRow.class);

        assertThat(query.list(null)).containsExactly(row1, row2);
    }

    @Test
    void shouldReturnOptionalEmptyWhenNoRows() {
        InMemoryQueries queries = new InMemoryQueries(Map.of(SampleRow.class, List.of()));

        Query<SampleRow> query = queries.forType(SampleRow.class);

        Optional<SampleRow> result = query.one(null);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnFirstRowWhenMultipleRowsExist() {
        SampleRow row1 = new SampleRow(1L, "one");
        SampleRow row2 = new SampleRow(2L, "two");

        InMemoryQueries queries = new InMemoryQueries(Map.of(SampleRow.class, List.of(row1, row2)));

        Query<SampleRow> query = queries.forType(SampleRow.class);

        Optional<SampleRow> result = query.one(null);

        assertThat(result).contains(row1);
    }
}
