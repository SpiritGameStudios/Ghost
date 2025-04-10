package dev.spiritstudios.ghost.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public class CommandContext {
	private final SlashCommandInteraction interaction;

	public CommandContext(SlashCommandInteraction interaction) {
		this.interaction = interaction;
	}

	// region Options
	public Optional<String> getStringOption(String name) {
		return getOption(name).map(OptionMapping::getAsString);
	}

	public Optional<Integer> getIntegerOption(String name) {
		return getOption(name).map(OptionMapping::getAsInt);
	}

	public Optional<Double> getDoubleOption(String name) {
		return getOption(name).map(OptionMapping::getAsDouble);
	}

	public Optional<Long> getLongOption(String name) {
		return getOption(name).map(OptionMapping::getAsLong);
	}

	public Optional<Boolean> getBooleanOption(String name) {
		return getOption(name).map(OptionMapping::getAsBoolean);
	}

	public Optional<User> getUserOption(String name) {
		return getOption(name).map(OptionMapping::getAsUser);
	}

	public Optional<Channel> getChannelOption(String name) {
		return getOption(name).map(OptionMapping::getAsChannel);
	}

	public Optional<Role> getRoleOption(String name) {
		return getOption(name).map(OptionMapping::getAsRole);
	}

	public Optional<IMentionable> getMentionableOption(String name) {
		return getOption(name).map(OptionMapping::getAsMentionable);
	}

	public Optional<Message.Attachment> getAttachmentOption(String name) {
		return getOption(name).map(OptionMapping::getAsAttachment);
	}

	private Optional<OptionMapping> getOption(String name) {
		return Optional.ofNullable(interaction.getOption(name));
	}
	// endregion

	// region Reply
	public ReplyCallbackAction defer() {
		return interaction.deferReply();
	}

	public ReplyCallbackAction reply(String content) {
		return interaction.reply(content);
	}

	public ReplyCallbackAction reply(MessageEmbed embed) {
		return interaction.replyEmbeds(embed);
	}

	public ReplyCallbackAction reply(List<MessageEmbed> embeds) {
		return interaction.replyEmbeds(embeds);
	}

	public ReplyCallbackAction reply(EmbedBuilder embed) {
		return interaction.replyEmbeds(embed.build());
	}
	// endregion

	public Member botMember() {
		return guild()
			.map(guild -> guild.getMember(api().getSelfUser()))
			.orElseThrow(() -> new IllegalStateException("Failed to get bot guild member. CommandContext#botMember() may have been run on a non-guild command."));
	}

	public boolean hasPermission(Permission... permissions) {
		return botMember().hasPermission(permissions);
	}

	public JDA api() {
		return interaction.getJDA();
	}

	public Optional<Guild> guild() {
		return Optional.ofNullable(interaction.getGuild());
	}

	public User user() {
		return interaction.getUser();
	}

	public Optional<Member> member() {
		return Optional.ofNullable(interaction.getMember());
	}

	public MessageChannelUnion channel() {
		return interaction.getChannel();
	}

	public String fullCommandName() {
		return interaction.getFullCommandName();
	}

	public OffsetDateTime timeCreated() {
		return interaction.getTimeCreated();
	}

	public SlashCommandInteraction interaction() {
		return interaction;
	}
}
