package io.github.ddmfuhrmann.queries.spring;

import io.github.ddmfuhrmann.queries.Queries;
import io.github.ddmfuhrmann.queries.spring.support.SpringTestRecords.SpringSampleRow;
import io.github.ddmfuhrmann.queries.spring.support.SpringTestRecords.SpringSampleRow.Params;
import io.github.ddmfuhrmann.queries.spring.support.SpringTestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringTestConfig.class)
class SpringJdbcQueriesIntegrationTest {

    private final Queries queries;

    SpringJdbcQueriesIntegrationTest(Queries queries) {
        this.queries = queries;
    }

    @Test
    void shouldReturnOneResultWithAllFieldsMapped() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");

        Optional<SpringSampleRow> result = queries
                .forType(SpringSampleRow.class)
                .one(new Params(id));

        assertThat(result).isPresent();
        SpringSampleRow row = result.orElseThrow();

        assertThat(row.id()).isEqualTo(id);
        assertThat(row.name()).isEqualTo("Alice");
        assertThat(row.birthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(row.amount()).isEqualByComparingTo(new BigDecimal("100.50"));
        assertThat(row.active()).isTrue();
        assertThat(row.description()).isEqualTo("First row");
    }

    @Test
    void shouldReturnEmptyOptionalWhenNoResult() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000099");

        Optional<SpringSampleRow> result = queries
                .forType(SpringSampleRow.class)
                .one(new Params(id));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldMapNullableFieldsToNull() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000002");

        Optional<SpringSampleRow> result = queries
                .forType(SpringSampleRow.class)
                .one(new Params(id));

        assertThat(result).isPresent();
        SpringSampleRow row = result.orElseThrow();

        assertThat(row.name()).isEqualTo("Bob");
        assertThat(row.active()).isNull();
        assertThat(row.description()).isNull();
    }

}