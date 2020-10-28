package fr.keyser.evolution.event;

import fr.keyser.evolution.engine.Event;
import fr.keyser.evolution.model.PlayerInputStatus;

public class PlayerStateChanged implements Event {

	private final int player;

	private final PlayerInputStatus state;

	public PlayerStateChanged(int player, PlayerInputStatus state) {
		this.player = player;
		this.state = state;
	}

	public PlayerInputStatus getState() {
		return state;
	}

	@Override
	public String toString() {
		return String.format("PlayerStateChanged [player=%s, state=%s]", player, state);
	}

	public int getPlayer() {
		return player;
	}
}
