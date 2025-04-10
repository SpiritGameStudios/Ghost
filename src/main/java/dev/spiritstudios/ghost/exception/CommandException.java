package dev.spiritstudios.ghost.exception;

/**
 * A non-fatal exception in a command.
 * The message should make sense to an average user, as it will be displayed.
 */
public abstract class CommandException extends RuntimeException {
	public CommandException(String message) {
		super(message);
	}
}
