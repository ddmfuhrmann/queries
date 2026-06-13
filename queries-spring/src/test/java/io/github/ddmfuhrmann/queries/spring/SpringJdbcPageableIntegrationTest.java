package io.github.ddmfuhrmann.queries.spring;

import io.github.ddmfuhrmann.queries.Queries;
import io.github.ddmfuhrmann.queries.page.Direction;
import io.github.ddmfuhrmann.queries.page.Order;
import io.github.ddmfuhrmann.queries.page.PageRequest;
import io.github.ddmfuhrmann.queries.spring.support.SpringTestConfig;
import io.github.ddmfuhrmann.queries.spring.support.SpringTestRecords.SpringSampleListRow;
import io.github.ddmfuhrmann.queries.spring.support.SpringTestRecords.SpringSampleListRow.Filter;
import io.github.ddmfuhrmann.queries.spring.support.SpringTestRecords.SpringSampleRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringTestConfig.class)
class SpringJdbcPageableIntegrationTest {

    private final Queries queries;

    SpringJdbcPageableIntegrationTest(Queries queries) {
        this.queries = queries;
    }

    @Test
    void shouldApplyDefaultSortAndDefaultPageSize() {
        // defaultSort = name,id  defaultPageSize = 2  → first two by name
        List<SpringSampleListRow> rows = queries
                .forType(SpringSampleListRow.class)
                .list(new Filter(null));

        assertThat(rows).extracting(SpringSampleListRow::name).containsExactly("Alice", "Bob");
    }

    @Test
    void shouldApplyExplicitSortAndWindow() {
        PageRequest page = PageRequest.of(List.of(new Order("amount", Direction.DESC)), 2, 0L);

        List<SpringSampleListRow> rows = queries
                .forType(SpringSampleListRow.class)
                .list(new Filter(null), page);

        assertThat(rows).extracting(SpringSampleListRow::name).containsExactly("Carol", "Bob");
    }

    @Test
    void shouldHonourOffset() {
        PageRequest page = PageRequest.of(List.of(Order.asc("name")), 2, 1L);

        List<SpringSampleListRow> rows = queries
                .forType(SpringSampleListRow.class)
                .list(new Filter(null), page);

        assertThat(rows).extracting(SpringSampleListRow::name).containsExactly("Bob", "Carol");
    }

    @Test
    void shouldCountIgnoringPagination() {
        long all = queries.forType(SpringSampleListRow.class).count(new Filter(null));
        long filtered = queries.forType(SpringSampleListRow.class).count(new Filter(new BigDecimal("200")));

        assertThat(all).isEqualTo(3);
        assertThat(filtered).isEqualTo(2);
    }

    @Test
    void shouldRejectUnknownSortColumn() {
        PageRequest page = PageRequest.of(List.of(Order.asc("dropTable")), 10, 0L);

        assertThatThrownBy(() -> queries.forType(SpringSampleListRow.class).list(new Filter(null), page))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("dropTable");
    }

    @Test
    void shouldRejectPageRequestOnStaticQuery() {
        PageRequest page = PageRequest.of(List.of(Order.asc("name")), 10, 0L);

        assertThatThrownBy(() -> queries.forType(SpringSampleRow.class).list(null, page))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not pageable");
    }
}
