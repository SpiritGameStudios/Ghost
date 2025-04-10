package dev.spiritstudios.ghost.registry;

import com.google.common.io.Resources;
import dev.spiritstudios.ghost.util.FileUtil;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public final class TagRegistry implements Registry<String> {
	private static final ObjectList<String> values = new ObjectArrayList<>();

	// May contain either an id or alias as the key
	private final Map<String, Integer> byName = new Object2IntOpenHashMap<>();

	private boolean frozen;

	@Override
	public String register(String id, String entry) {
		if (frozen) throw new IllegalStateException("Attempted to register object after registry was frozen");

		values.add(entry);
		byName.put(id, values.size() - 1);

		return entry;
	}

	public void registerAlias(String alias, String id) {
		if (frozen) throw new IllegalStateException("Attempted to register object after registry was frozen");

		byName.put(alias, byName.get(id));
	}

	public void load() {
		URI uri;
		try {
			uri = Resources.getResource("tags").toURI();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		try {
			loadInternal(Paths.get(uri));
		} catch (FileSystemNotFoundException e) {
			// If we get here, we are running from a jar
			try (FileSystem fs = FileSystems.newFileSystem(uri, new HashMap<>())) {
				loadInternal(fs.getPath("tags"));
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	private void loadInternal(Path tagsPath) {
		List<Path> paths;

		try (Stream<Path> walk = Files.list(tagsPath)) {
			paths = walk
				.map(tagsPath::relativize)
				.toList();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		if (paths.isEmpty()) throw new IllegalStateException("Tags folder is empty");

		Map<String, String> alias = new Object2ObjectOpenHashMap<>();

		for (Path path : paths) {
			String content = FileUtil.getResource("tags/" + path.toString(), getClass().getClassLoader());
			if (path.toString().endsWith(".md")) {
				register(
					path.toString().replace(".md", ""),
					content
				);

				continue;
			}

			if (path.toString().endsWith(".alias"))
				alias.put(path.toString().replace(".alias", ""), content);
		}

		alias.forEach(this::registerAlias);
	}

	@Override
	public void freeze() {
		if (frozen) throw new IllegalStateException("Registry already frozen");
		frozen = true;
	}

	@Override
	public Optional<String> get(String id) {
		return Optional.ofNullable(values.get(byName.get(id)));
	}

	@Override
	public Set<String> keySet() {
		return byName.keySet();
	}

	@Override
	public @NotNull Iterator<String> iterator() {
		return values.iterator();
	}
}
