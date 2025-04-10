package dev.spiritstudios.ghost.command.tool;

import dev.spiritstudios.ghost.command.Command;
import dev.spiritstudios.ghost.command.CommandContext;
import dev.spiritstudios.ghost.data.CommonColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;
import java.time.temporal.ChronoUnit;

public class PingCommand implements Command {
	@Override
	public String getName() {
		return "ping";
	}

	@Override
	public SlashCommandData createSlashCommand() {
		return Commands.slash(getName(), "See the bot's latency");
	}

	@Override
	public void execute(CommandContext context) {
		context.channel().sendMessage("Pinging...").queue(message -> {
			long roundtripLatency = ChronoUnit.MILLIS.between(context.timeCreated(), message.getTimeCreated());
			long gatewayLatency = context.api().getGatewayPing();

			Color color = roundtripLatency < 150 ?
				CommonColors.GREEN :
				roundtripLatency < 250 ?
					CommonColors.YELLOW :
					CommonColors.RED;

			message.delete().queue(ignored -> context.reply(new EmbedBuilder()
				.setTitle("Pong!")
				.addField("Roundtrip Latency", "%dms".formatted(roundtripLatency), true)
				.addField("Gateway Latency", "%dms".formatted(gatewayLatency), true)
				.setColor(color)).queue());
		});
	}
}
