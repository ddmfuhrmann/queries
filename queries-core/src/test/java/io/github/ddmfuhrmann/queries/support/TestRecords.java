package io.github.ddmfuhrmann.queries.support;

import io.github.ddmfuhrmann.queries.annotation.QueriesColumn;
import io.github.ddmfuhrmann.queries.annotation.QueriesResource;

@QueriesResource("sql/sample-row.sql")
public final class TestRecords {

    private TestRecords() {}

    @QueriesResource("sql/sample-row.sql")
    public record SampleRow(
            @QueriesColumn("id") Long id,
            @QueriesColumn("name") String name
    ) {}

    public record NoResourceRow(Long id) {}

    @QueriesResource("sql/sample-row.sql")
    public record MissingColumnAnnotationRow(Long id) {}

    public record SimpleParams(String name, int age) {}

    public record NullableParams(String name, String description) {}
}

