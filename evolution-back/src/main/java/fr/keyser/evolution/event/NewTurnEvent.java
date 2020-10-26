package fr.keyser.evolution.event;

public class NewTurnEvent implements TurnEvent {

	private final int firstPlayer;

	public NewTurnEvent(int firstPlayer) {
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
