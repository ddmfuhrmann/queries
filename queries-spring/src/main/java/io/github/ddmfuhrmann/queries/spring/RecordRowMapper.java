package io.github.ddmfuhrmann.queries.spring;

import io.github.ddmfuhrmann.queries.row.QueriesRecordConstructor;
import io.github.ddmfuhrmann.queries.row.QueriesRowMetadata;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * {@link RowMapper} implementation that maps a {@link ResultSet} row
 * to a Java record instance using reflection and column metadata.
 * <p>
 * The constructor of the target record is resolved once and reused
 * for each row mapping.
 */
final class RecordRowMapper<T> implements RowMapper<T> {

    private final Constructor<T> constructor;
    private final Class<?>[] parameterTypes;
    private final List<String> columns;

    public RecordRowMapper(Class<T> rowType, QueriesRowMetadata metadata) {
        Objects.requireNonNull(rowType, "rowType must not be null");
        Objects.requireNonNull(metadata, "metadata must not be null");

        this.constructor = QueriesRecordConstructor.resolve(rowType);
        this.parameterTypes = this.constructor.getParameterTypes();
        this.columns = List.copyOf(metadata.columns().values());
    }

    /**
     * Maps the current row of the {@link ResultSet} to a record instance.
     * <p>
     * Values are read in the same order as the record parameters and
     * converted when necessary by {@link #readValue(ResultSet, String, Class)}.
     *
     * @throws IllegalStateException if the record cannot be instantiated
     */
    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        Object[] args = new Object[columns.size()];

        for (int i = 0; i < columns.size(); i++) {
            String column = columns.get(i);
            Class<?> targetType = parameterTypes[i];

            args[i] = readValue(rs, column, targetType);
        }

        try {
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to map record %s at row %d"
                    .formatted(constructor.getDeclaringClass().getName(), rowNum), e);
        }
    }

    /**
     * Reads a column from the {@link ResultSet} and converts legacy JDBC date/time
     * types to Java time API types when required.
     * <p>
     * {@link java.sql.Date} is converted to {@link java.time.LocalDate} and
     * {@link java.sql.Timestamp} to {@link java.time.LocalDateTime} when the
     * record parameter expects these types. Other values are returned as is.
     */
    private Object readValue(ResultSet rs, String column, Class<?> targetType)
            throws SQLException {
        Object value = rs.getObject(column);

        return switch (value) {
            case null -> null;
            case java.sql.Date date when targetType.equals(LocalDate.class) -> date.toLocalDate();
            case Timestamp ts when targetType.equals(LocalDateTime.class) -> ts.toLocalDateTime();
            default -> value;
        };
    }

}