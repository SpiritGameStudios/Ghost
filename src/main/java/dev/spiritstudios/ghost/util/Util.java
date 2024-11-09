package dev.spiritstudios.ghost.util;

import java.util.function.Consumer;

/**
 * Utility functions that don't fit anywhere else
 */
public final class Util {
    public static void utilError() {
        throw new UnsupportedOperationException("Tried to instantiate utility class");
    }

    /**
     * Calls initializer, then returns object.
     * Useful for initializing static fields without using a static block.
     */
    public static <T> T make(T object, Consumer<? super T> initializer) {
        initializer.accept(object);
        return object;
    }
}
