# Queries

**Queries** is a lightweight Java library for implementing **read-side queries** using **explicit query resources** and **Java records**, without ORM, proxies, or code generation.

It is designed for applications that adopt **CQRS**, **hexagonal architecture**, or simply want a clear separation between **read models** and **write models**, keeping SQL readable and intentional.

---

## Goals

The main goals of Queries are:

- Make SQL explicit and first-class
- Use Java records as immutable read models
- Avoid ORMs, proxies, and runtime magic
- Provide a small, predictable API
- Keep infrastructure concerns isolated
- Favor clarity over flexibility

---

## What Queries Is (and Is Not)

### Queries is:
- A read-side query execution library
- SQL-first and record-oriented
- Suitable for CQRS read models
- Framework-agnostic at the core
- Explicit and fail-fast

### Queries is not:
- An ORM
- A query DSL
- A dynamic criteria builder
- A reporting engine
- A replacement for JPA repositories

---

## Architecture Overview

Queries is built as a **multi-module Maven project**:

```
queries
├─ queries-core     (framework-agnostic)
└─ queries-spring   (Spring JDBC adapter)
```

### queries-core
- Public API (`Queries`, `Query`)
- Query metadata resolution
- Query resource loading
- Parameter resolution from records
- No dependency on Spring or JDBC

### queries-spring
- Spring JDBC implementation
- Integration with `NamedParameterJdbcTemplate`
- Mapping from `ResultSet` to records

---

## Core Concepts

### Query Row

Each query is represented by a **record** that models the result row.

```java
@QueriesResource("queries/party-summary.sql")
public record PartySummaryRow(
    @QueriesColumn("party_id") UUID id,
    @QueriesColumn("name") String name,
    @QueriesColumn("active") boolean active
) {

    public record Params(UUID id) {}
}
```

- The record defines the shape of the result
- Each column must be explicitly mapped
- The SQL resource defines the query contract

---

### Query Params

Query parameters are represented as **records**.

- Parameters are resolved by name
- No maps or positional parameters are exposed
- The params record is typically nested inside the row record

```java
new PartySummaryRow.Params(id)
```

This ensures:
- High cohesion
- Clear ownership of query inputs
- IDE discoverability
- Safe refactoring

---

### Fluent API

Queries uses a small fluent API:

```java
PartySummaryRow row =
    queries.forType(PartySummaryRow.class)
           .one(new PartySummaryRow.Params(id))
           .orElseThrow();
```

Or for multiple results:

```java
List<PartySummaryRow> rows =
    queries.forType(PartySummaryRow.class)
           .list(new PartySummaryRow.Params(id));
```

---

## Public API (v0.1.0)

```java
public interface Queries {

    <T> Query<T> forType(Class<T> rowType);
}
```

```java
public interface Query<T> {

    List<T> list(Object params);

    List<T> list(Object params, PageRequest page); // pageable rows only

    Optional<T> one(Object params);

    long count(Object params);
}
```

- The row type defines the query
- Parameters are passed as records
- `count` is derived from the same SQL (`SELECT COUNT(*) FROM (...)`) — no count record
- `list(params, page)` and dynamic sort require `@QueriesPageable` on the row

---

## Annotations

### @QueriesResource

Defines the classpath resource that contains the query.

```java
@QueriesResource("queries/party-summary.sql")
```

- Mandatory
- Fail-fast if missing or invalid

---

### @QueriesColumn

Defines the column alias expected from the query result.

```java
@QueriesColumn("party_id")
```

- Mandatory for all record components
- Enforces explicit mapping (snake_case friendly)
- Prevents implicit naming assumptions

---

### @QueriesPageable (v0.2.0)

Opts a row type into library-owned sorting, paging and count. The `.sql` resource
must be the **base query only** — no `ORDER BY`/`LIMIT`/`OFFSET`.

```java
@QueriesResource("queries/city-list.sql")
@QueriesPageable(defaultSort = {"cityName", "id"}, defaultPageSize = 50)
public record CityListRow(
        @QueriesColumn("id") Integer id,
        @QueriesColumn("cityName") String cityName
) {}
```

```java
// explicit window + sort (sort fields must be @QueriesColumn aliases)
var page = PageRequest.of(List.of(Order.asc("cityName")), 20, 0L);
List<CityListRow> rows = queries.forType(CityListRow.class).list(filter, page);
long total            = queries.forType(CityListRow.class).count(filter);

// no PageRequest → defaultSort + defaultPageSize
List<CityListRow> firstPage = queries.forType(CityListRow.class).list(filter);
```

- `defaultSort` aliases double as the **sort allow-list** (unknown field → `IllegalArgumentException`)
- A row **without** the annotation is static: passing a `PageRequest` fails fast
- `count` works on any row; for pageable rows it simply wraps the base query
- The core stays framework-agnostic (`Direction`/`Order`/`PageRequest` in `…queries.page`);
  Spring's `Pageable` is adapted by the caller

---

## Design Decisions (v0.1.0)

- SQL is external and explicit
- Column mapping is name-based, not positional
- All mappings are explicit and fail-fast
- No dynamic WHERE clauses in core
- No optional parameters by default
- No proxies or runtime bytecode generation
- Reflection is used only for metadata resolution
- Metadata is cached and immutable

---

## What Is Intentionally Out of Scope

For version 0.1.0, Queries does not include:

- Dynamic filter builders
- Drill-down or exploratory search DSLs
- Pagination helpers
- Sorting abstractions
- Caching strategies
- Query composition

These features may be explored in future versions or separate modules.

---

## When to Use Queries

Queries works best when:

- Read models are distinct from write models
- SQL clarity matters more than abstraction
- The system follows CQRS or similar patterns
- You want explicit control over queries
- You want predictable and debuggable behavior

---
## Modules
### Spring JDBC Integration

Detailed documentation for the Spring integration (bean configuration, usage examples, mapping strategy, and H2-based tests) is available in `queries-spring/README.md`.

Key topics covered:

- Required Maven dependencies (`queries-spring` and `spring-jdbc`)
- Explicit configuration of the `Queries` bean with `NamedParameterJdbcTemplate`
- Usage in Spring services
- Column and parameter mapping strategy
- Integration testing with Spring and H2

---

## Version

Current version: **0.2.0**

- **0.2.0** — `@QueriesPageable`: library-owned sorting, paging and derived `count`
  (framework-agnostic `page` types; base-query-only SQL; sort allow-list)
- **0.1.0** — core abstractions, metadata resolution, fluent API, Spring JDBC
  integration, strong contracts and fail-fast behavior

---

## Next Steps

Planned next steps for future versions of Queries include:

- Provide a **pure JDBC implementation** without any framework dependency
- Add a **Micronaut-based implementation** aligned with Micronaut Data and JDBC support

These steps will be implemented incrementally, keeping the core module stable and framework-agnostic.

---

## License

This project is open source and available under the MIT License.
