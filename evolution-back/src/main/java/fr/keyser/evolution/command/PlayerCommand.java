package fr.keyser.evolution.command;

public class PlayerCommand {
	private final int player;

	private final Command command;

	public PlayerCommand(int player, Command command) {
		this.player = player;
		this.command = command;
	}

	public int getPlayer() {
		return player;
	}

	public Command getCommand() {
		return command;
	}

}
