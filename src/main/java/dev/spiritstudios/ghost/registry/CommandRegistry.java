package dev.spiritstudios.ghost.registry;

import dev.spiritstudios.ghost.Ghost;
import dev.spiritstudios.ghost.GhostConfig;
import dev.spiritstudios.ghost.command.Command;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.ApplicationCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
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

	public void sendCommands(DiscordApi api) {
		Set<SlashCommandBuilder> builders = byName.values().stream()
			.map(index -> values.get(index).createSlashCommand())
			.collect(Collectors.toSet());

		LOGGER.trace("Sending commands to discord");
		Set<ApplicationCommand> registeredCommands = (GhostConfig.INSTANCE.debug()
			? api.bulkOverwriteServerApplicationCommands(GhostConfig.INSTANCE.guildId(), builders)
			: api.bulkOverwriteGlobalApplicationCommands(builders)).join();

		if (GhostConfig.INSTANCE.debug())
			api.bulkOverwriteGlobalApplicationCommands(Set.of()).join();

		for (ApplicationCommand command : registeredCommands) {
			byId.put(command.getId(), byName.get(command.getName()));
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
}
