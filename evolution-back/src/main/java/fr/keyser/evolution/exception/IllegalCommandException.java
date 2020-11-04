package fr.keyser.evolution.exception;

import java.util.Map;

import fr.keyser.evolution.command.Command;

public class IllegalCommandException extends EvolutionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4694555817375667960L;
	private final String message;
	private final Command command;

	public IllegalCommandException(String message, Command command) {
		super("illegal_command");
		this.message = message;
		this.command = command;
	}

	@Override
	public void message(Map<String, Object> out) {
		super.message(out);
		out.put("message", message);
		out.put("command", command);
	}

}
