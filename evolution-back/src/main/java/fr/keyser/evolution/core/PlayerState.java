package fr.keyser.evolution.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.event.PlayerStateChanged;
import fr.keyser.evolution.model.PlayerInputStatus;

public class PlayerState {
	public final static PlayerState INITIAL = new PlayerState(PlayerInputStatus.IDLE);

	private final PlayerInputStatus state;

	@JsonCreator
	public PlayerState(@JsonProperty("state") PlayerInputStatus state) {
		this.state = state;
	}

	public PlayerState accept(PlayerStateChanged changed) {
		return new PlayerState(changed.getState());
	}

	public PlayerInputStatus getState() {
		return state;
	}
}
