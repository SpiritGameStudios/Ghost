package dev.spiritstudios.ghost.registry;

import java.util.Optional;

public interface Registry<T> extends Iterable<T> {
    T register(String id, T entry);
    void freeze();
    Optional<T> get(String id);
}
