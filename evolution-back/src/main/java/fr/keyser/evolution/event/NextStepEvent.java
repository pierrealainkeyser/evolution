package fr.keyser.evolution.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.core.TurnStep;

public class NextStepEvent implements TurnEvent {

	private final TurnStep step;

	private final int currentPlayer;

	@JsonCreator
	public NextStepEvent(@JsonProperty("step") TurnStep step, @JsonProperty("currentPlayer") int currentPlayer) {
		this.step = step;
		this.currentPlayer = currentPlayer;
	}

	public TurnStep getStep() {
		return step;
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}

	@Override
	public String toString() {
		return String.format("NextStepEvent [step=%s, currentPlayer=%s]", step, currentPlayer);
	}
}
