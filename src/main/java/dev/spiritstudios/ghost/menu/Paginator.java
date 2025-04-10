package dev.spiritstudios.ghost.menu;

import dev.spiritstudios.ghost.listener.ButtonListener;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.List;

public class Paginator {
	private static int GLOBAL_INDEX = 0;

	private int page = 0;
	private final List<MessageEditData> pages;
	private final int index;

	public Paginator(List<MessageEditData> pages) {
		this.pages = pages;
		this.index = GLOBAL_INDEX;
		GLOBAL_INDEX++;
	}

	public void send(IReplyCallback replyCallback) {
		Button left = Button.primary("left" + index, Emoji.fromUnicode("◀"));
		Button right = Button.primary("right" + index, Emoji.fromUnicode("▶"));

		MessageEditData data = pages.get(page);
		ReplyCallbackAction action = replyCallback.reply(
			new MessageCreateBuilder()
				.setContent(data.getContent())
				.setEmbeds(data.getEmbeds())
				.setComponents(data.getComponents())
				.setAllowedMentions(data.getAllowedMentions())
				.build()
		);

		action.addActionRow(left, right);
		action.queue(hook -> {
			ButtonListener.register(left.getId(), this::left);
			ButtonListener.register(right.getId(), this::right);
		});
	}

	private void edit(IMessageEditCallback action) {
		MessageEditData data = pages.get(page);
		action.editMessage(data).queue();
	}

	private void left(ButtonInteraction interaction) {
		page--;
		page = Math.clamp(page, 0, pages.size() - 1);

		edit(interaction);
	}

	private void right(ButtonInteraction interaction) {
		page++;
		page = Math.clamp(page, 0, pages.size() - 1);

		edit(interaction);
	}
}
