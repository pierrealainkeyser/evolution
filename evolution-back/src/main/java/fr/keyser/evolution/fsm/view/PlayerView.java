package fr.keyser.evolution.fsm.view;

import java.util.List;

import fr.keyser.evolution.model.PlayerInputStatus;
import fr.keyser.security.AuthenticatedPlayer;

public class PlayerView {

	private final int index;

	private final AuthenticatedPlayer player;

	private final int inHands;

	private final PlayerInputStatus status;

	private final List<SpecieView> species;

	public PlayerView(int index, AuthenticatedPlayer player, PlayerInputStatus status, int inHands,
			List<SpecieView> species) {
		this.index = index;
		this.player = player;
		this.status = status;
		this.inHands = inHands;
		this.species = species;
	}

	public int getIndex() {
		return index;
	}

	public AuthenticatedPlayer getPlayer() {
		return player;
	}

	public int getInHands() {
		return inHands;
	}

	public List<SpecieView> getSpecies() {
		return species;
	}

	public PlayerInputStatus getStatus() {
		return status;
	}
}
