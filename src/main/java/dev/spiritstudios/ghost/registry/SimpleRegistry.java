package dev.spiritstudios.ghost.registry;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.javacord.api.listener.GloballyAttachableListener;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class SimpleRegistry<T> implements Registry<T> {
    private final Map<String, T> byId = new Object2ObjectOpenHashMap<>();
    private boolean frozen;

    @Override
    public T register(String id, T entry) {
        if (this.frozen)
            throw new IllegalStateException("Attempted to register object after registry was frozen");

        byId.put(id, entry);
        return entry;
    }

    @Override
    public void freeze() {
        frozen = true;
    }

    @Override
    public Optional<T> get(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return byId.values().iterator();
    }
}
