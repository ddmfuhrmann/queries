package io.github.ddmfuhrmann.queries;

public interface Queries {

    <T> Query<T> forType(Class<T> rowType);

}