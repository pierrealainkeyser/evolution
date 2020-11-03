package fr.keyser.evolution.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LastTurnEvent implements TurnEvent {

	private final boolean lastTurn;

	@JsonCreator
	public LastTurnEvent(@JsonProperty("lastTurn") boolean lastTurn) {
		this.lastTurn = lastTurn;
	}

	public boolean isLastTurn() {
		return lastTurn;
	}

	@Override
	public String toString() {
		return String.format("LastTurnEvent [lastTurn=%s]", lastTurn);
	}
}
