package fr.keyser.evolution.event;

import fr.keyser.evolution.engine.Event;

public class PlayerPassedEvent implements Event {

	private final int player;

	public PlayerPassedEvent(int player) {
		this.player = player;
	}

	public int getPlayer() {
		return player;
	}

	@Override
	public String toString() {
		return String.format("PlayerPassedEvent [player=%s]", player);
	}
}
