package dev.spiritstudios.ghost.registry;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Registry<T> extends Iterable<T> {
	T register(String id, T entry);

	void freeze();

	Optional<T> get(String id);

	Set<String> keySet();

	default Stream<T> stream() {
		return StreamSupport.stream(this.spliterator(), false);
	}

	default Stream<T> parallelStream() {
		return StreamSupport.stream(this.spliterator(), true);
	}
}
