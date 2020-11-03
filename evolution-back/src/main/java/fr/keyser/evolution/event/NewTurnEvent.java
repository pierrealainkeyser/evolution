package fr.keyser.evolution.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NewTurnEvent implements TurnEvent {

	private final int firstPlayer;

	@JsonCreator
	public NewTurnEvent(@JsonProperty("firstPlayer") int firstPlayer) {
		this.firstPlayer = firstPlayer;
	}

	public int getFirstPlayer() {
		return firstPlayer;
	}

	@Override
	public String toString() {
		return String.format("NewTurnEvent [firstPlayer=%s]", firstPlayer);
	}
}
