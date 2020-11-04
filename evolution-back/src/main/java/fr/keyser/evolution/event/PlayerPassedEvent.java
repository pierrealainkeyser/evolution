package fr.keyser.evolution.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.engine.Event;

public class PlayerPassedEvent implements Event {
	
	private final int player;

	@JsonCreator
	public PlayerPassedEvent(@JsonProperty("player") int player) {
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
