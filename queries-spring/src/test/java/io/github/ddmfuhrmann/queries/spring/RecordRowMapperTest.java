package io.github.ddmfuhrmann.queries.spring;

import io.github.ddmfuhrmann.queries.row.QueriesRowMetadata;
import io.github.ddmfuhrmann.queries.spring.support.SpringTestRecords;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RecordRowMapperTest {

    @Test
    void shouldMapRowWithSupportedTypes() throws Exception {
        QueriesRowMetadata metadata = QueriesRowMetadata.of(SpringTestRecords.SpringSampleRow.class);
        RecordRowMapper<SpringTestRecords.SpringSampleRow> mapper =
                new RecordRowMapper<>(SpringTestRecords.SpringSampleRow.class, metadata);

        ResultSet rs = mock(ResultSet.class);
        when(rs.getObject("id")).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        when(rs.getObject("name")).thenReturn("Alice");

        java.sql.Date sqlDate = java.sql.Date.valueOf(LocalDate.of(1990, 1, 1));
        Timestamp ts = Timestamp.valueOf(LocalDateTime.of(2020, 3, 3, 10, 15));

        when(rs.getObject("birth_date_legacy")).thenReturn(sqlDate);
        when(rs.getObject("created_at_legacy")).thenReturn(ts);
        when(rs.getObject("amount")).thenReturn(new BigDecimal("100.50"));
        when(rs.getObject("active")).thenReturn(Boolean.TRUE);
        when(rs.getObject("description")).thenReturn("First row");

        SpringTestRecords.SpringSampleRow row = mapper.mapRow(rs, 0);

        assertThat(row.id()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        assertThat(row.name()).isEqualTo("Alice");
        assertThat(row.birthDate()).isEqualTo(sqlDate.toLocalDate());
        assertThat(row.createdAt()).isEqualTo(ts.toLocalDateTime());
        assertThat(row.amount()).isEqualByComparingTo(new BigDecimal("100.50"));
        assertThat(row.active()).isTrue();
        assertThat(row.description()).isEqualTo("First row");
    }

    @Test
    void shouldWrapMappingErrorsInIllegalStateException() throws Exception {
        QueriesRowMetadata metadata = QueriesRowMetadata.of(SpringTestRecords.SpringSampleRow.class);
        RecordRowMapper<SpringTestRecords.SpringSampleRow> mapper =
                new RecordRowMapper<>(SpringTestRecords.SpringSampleRow.class, metadata);

        try (ResultSet rs = mock(ResultSet.class)) {
            when(rs.getObject("id")).thenReturn("not-a-uuid");
            when(rs.getObject("name")).thenReturn("Alice");
            when(rs.getObject("birth_date_legacy")).thenReturn(java.sql.Date.valueOf(LocalDate.of(1990, 1, 1)));
            when(rs.getObject("created_at_legacy")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
            when(rs.getObject("amount")).thenReturn(new BigDecimal("100.50"));
            when(rs.getObject("active")).thenReturn(Boolean.TRUE);
            when(rs.getObject("description")).thenReturn("First row");

            assertThatThrownBy(() -> mapper.mapRow(rs, 0))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Failed to map record %s at row %d"
                            .formatted(SpringTestRecords.SpringSampleRow.class.getName(), 0));
        }
    }

}