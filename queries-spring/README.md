## Spring Integration

Queries provides a Spring-based adapter built on **Spring JDBC**, keeping the core module completely framework-agnostic.

The Spring integration lives in the `queries-spring` module and implements the `Queries` interface using `NamedParameterJdbcTemplate`.

---

### Required Dependencies

To use Queries with Spring, add the `queries-spring` module and Spring JDBC to your project.

Example using Maven:

```xml
<dependencies>
  <dependency>
    <groupId>io.github.ddmfuhrmann</groupId>
    <artifactId>queries-spring</artifactId>
    <version>0.1.0</version>
  </dependency>

  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
  </dependency>
</dependencies>
```

The adapter does **not** require:
- Spring Boot starters
- Spring Data
- Spring context scanning
- Transactions

Only Spring JDBC is used.

---

### Spring Configuration

Queries does not provide auto-configuration in version `0.1.0`.  
The `Queries` instance must be explicitly created as a Spring bean.

Example configuration:

```java
@Configuration
public class QueriesConfig {

    @Bean
    public Queries queries(NamedParameterJdbcTemplate jdbcTemplate) {
        return new SpringJdbcQueries(jdbcTemplate);
    }
}
```

This keeps configuration explicit and avoids hidden behavior.

---

### Usage with Spring

Once configured, the `Queries` bean can be injected and used directly.

```java
@Service
public class PartyQueryService {

    private final Queries queries;

    public PartyQueryService(Queries queries) {
        this.queries = queries;
    }

    public PartySummaryRow findById(UUID id) {
        return queries
            .forType(PartySummaryRow.class)
            .one(new PartySummaryRow.Params(id))
            .orElseThrow();
    }
}
```

---

### Mapping Strategy

The Spring adapter follows these rules:

- SQL is loaded from the classpath using `@QueriesResource`
- Parameters are resolved from record components by name
- Columns are mapped explicitly using `@QueriesColumn`
- Result mapping is based on column **names**, not positions
- Record instantiation uses the canonical constructor

If a query mapped with `one()` returns more than one row, an exception is thrown to enforce query uniqueness.

---

### Testing with Spring & H2

The `queries-spring` module includes integration tests that run against an in-memory **H2** database using Spring's testing support:

- A dedicated `@TestConfiguration` (`SpringTestConfig`) wires a `DataSource`, `NamedParameterJdbcTemplate` and `Queries` (`SpringJdbcQueries`).
- Schema and sample data are loaded from `src/test/resources/sql/spring-sample-schema.sql`.
- Record mapping is verified with `SpringSampleRow`, which includes `UUID`, `LocalDate`, `BigDecimal` and nullable columns.

You can run the tests for this module with:

```bash
mvn test -pl queries-spring
```

---

### Design Notes

- The Spring adapter is a thin layer over `queries-core`
- No Spring annotations are required on query records
- No proxies or runtime bytecode generation are used
- Reflection is limited to metadata resolution and record construction
- Metadata is cached and immutable

Future versions may introduce optional auto-configuration, but explicit wiring is intentional in `v0.1.0`.
