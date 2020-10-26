package fr.keyser.evolution.event;

public class NextPlayerEvent implements TurnEvent {

	private final int currentPlayer;

	public NextPlayerEvent(int currentPlayer) {
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
