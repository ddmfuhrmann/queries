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

    Optional<T> one(Object params);
}
```

- The row type defines the query
- Parameters are passed as records
- No overloads for single parameters
- No dynamic SQL composition

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

## Version

Current version: **0.1.0**

This version focuses on:
- Core abstractions
- Metadata resolution
- Fluent API
- Spring JDBC integration
- Strong contracts and fail-fast behavior

---

## Next Steps

Planned next steps for future versions of Queries include:

- Implement the **Spring JDBC adapter** (`queries-spring`) using `NamedParameterJdbcTemplate`
- Provide a **pure JDBC implementation** without any framework dependency
- Add a **Micronaut-based implementation** aligned with Micronaut Data and JDBC support

These steps will be implemented incrementally, keeping the core module stable and framework-agnostic.

---

## License

This project is open source and available under the MIT License.
