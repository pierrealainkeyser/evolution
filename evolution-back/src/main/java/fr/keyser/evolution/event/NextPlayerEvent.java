package fr.keyser.evolution.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NextPlayerEvent implements TurnEvent {

	private final int currentPlayer;

	@JsonCreator
	public NextPlayerEvent(@JsonProperty("currentPlayer") int currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}

	@Override
	public String toString() {
		return String.format("NextPlayerEvent [currentPlayer=%s]", currentPlayer);
	}
}
