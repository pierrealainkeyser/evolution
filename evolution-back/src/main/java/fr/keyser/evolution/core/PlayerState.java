package fr.keyser.evolution.core;

import fr.keyser.evolution.event.PlayerStateChanged;
import fr.keyser.evolution.model.PlayerInputStatus;

public class PlayerState {
	public final static PlayerState INITIAL = new PlayerState(PlayerInputStatus.IDLE);

	private final PlayerInputStatus state;

	private PlayerState(PlayerInputStatus state) {
		this.state = state;
	}

	public PlayerState accept(PlayerStateChanged changed) {
		return new PlayerState(changed.getState());
	}

	public PlayerInputStatus getState() {
		return state;
	}
}
