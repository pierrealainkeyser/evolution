package fr.keyser.evolution.event;

public class LastTurnEvent implements TurnEvent {

	private final boolean lastTurn;

	public LastTurnEvent(boolean lastTurn) {
		this.lastTurn = lastTurn;
	}

	public boolean isLastTurn() {
		return lastTurn;
	}
}
