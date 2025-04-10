package dev.spiritstudios.ghost.registry;

import dev.spiritstudios.ghost.GhostConfig;
import dev.spiritstudios.ghost.command.Command;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class CommandRegistry implements Registry<Command> {
	private static final Logger LOGGER = LogManager.getLogger(CommandRegistry.class);

	private final ObjectList<Command> values = new ObjectArrayList<>();

	private final Map<String, Integer> byName = new Object2IntOpenHashMap<>();
	private final Map<Long, Integer> byId = new Long2IntOpenHashMap();

	private boolean frozen;

	@Override
	public Command register(String id, Command entry) {
		if (frozen) throw new IllegalStateException("Attempted to register object after registry was frozen");

		values.add(entry);
		byName.put(id, values.size() - 1);

		return entry;
	}

	@Override
	public void freeze() {
		if (frozen) throw new IllegalStateException("Registry already frozen");
		frozen = true;
	}

	public void sendCommands(JDA api) {
		Set<SlashCommandData> data = byName.values().stream()
			.map(index -> values.get(index).createSlashCommand())
			.collect(Collectors.toSet());

		LOGGER.trace("Sending commands to discord");

		List<net.dv8tion.jda.api.interactions.commands.Command> registeredCommands = (GhostConfig.INSTANCE.debug()
			? Objects.requireNonNull(api.getGuildById(GhostConfig.INSTANCE.guildId())).updateCommands()
			: api.updateCommands()).addCommands(data).complete();

		for (var command : registeredCommands) {
			byId.put(command.getIdLong(), byName.get(command.getName()));
			LOGGER.trace("Initialized command {} with ID {}", command.getName(), command.getId());
		}
	}

	@Override
	public Optional<Command> get(String id) {
		return Optional.ofNullable(values.get(byName.get(id)));
	}

	public Optional<Command> get(long id) {
		return Optional.ofNullable(values.get(byId.get(id)));
	}

	@NotNull
	@Override
	public Iterator<Command> iterator() {
		return values.iterator();
	}

	@Override
	public Set<String> keySet() {
		return byName.keySet();
	}
}
