package io.github.ddmfuhrmann.queries.row;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.Objects;
import java.util.stream.Stream;

public class QueriesRecordConstructor {

    private QueriesRecordConstructor() {}

    @SuppressWarnings("java:S3011")
    public static <T> Constructor<T> resolve(Class<T> recordType) {
        Objects.requireNonNull(recordType, "recordType must not be null");

        if (!recordType.isRecord()) {
            throw new IllegalArgumentException("Type must be a record: %s".formatted(recordType.getName()));
        }

        try {
            Class<?>[] parameterTypes =
                    Stream.of(recordType.getRecordComponents())
                            .map(RecordComponent::getType)
                            .toArray(Class<?>[]::new);

            Constructor<T> constructor = recordType.getDeclaredConstructor(parameterTypes);

            // Required to access the canonical constructor of records via reflection
            constructor.setAccessible(true);

            return constructor;
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Failed to resolve canonical constructor for record: %s".formatted(recordType), e);
        }
    }

}