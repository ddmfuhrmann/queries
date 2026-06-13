package io.github.ddmfuhrmann.queries.spring.support;

import io.github.ddmfuhrmann.queries.annotation.QueriesColumn;
import io.github.ddmfuhrmann.queries.annotation.QueriesPageable;
import io.github.ddmfuhrmann.queries.annotation.QueriesResource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public final class SpringTestRecords {

    private SpringTestRecords() {}

    /** Pageable list row: base SQL only, library owns sort + page window. */
    @QueriesResource("sql/spring-sample-list.sql")
    @QueriesPageable(defaultSort = {"name", "id"}, defaultPageSize = 2)
    public record SpringSampleListRow(
            @QueriesColumn("id") UUID id,
            @QueriesColumn("name") String name,
            @QueriesColumn("amount") BigDecimal amount
    ) {
        public record Filter(BigDecimal minAmount) {}
    }

    @QueriesResource("sql/spring-sample-row.sql")
    public record SpringSampleRow(
            @QueriesColumn("id") UUID id,
            @QueriesColumn("name") String name,
            @QueriesColumn("birth_date_legacy") LocalDate birthDate,
            @QueriesColumn("created_at_legacy") LocalDateTime createdAt,
            @QueriesColumn("amount") BigDecimal amount,
            @QueriesColumn("active") Boolean active,
            @QueriesColumn("description") String description
    ) {
        public record Params(UUID id) {}
    }

}