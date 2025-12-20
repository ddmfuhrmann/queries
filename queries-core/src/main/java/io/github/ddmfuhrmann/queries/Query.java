package io.github.ddmfuhrmann.queries;

import java.util.List;
import java.util.Optional;

public interface Query<T> {

    List<T> list(Object params);

    Optional<T> one(Object params);

}