package fr.keyser.evolution.fsm;

import java.util.List;

public class ResolvedRef {

	private final PlayerRef myself;

	private final GameRef game;

	public ResolvedRef(PlayerRef myself, GameRef game) {
		this.myself = myself;
		this.game = game;
	}

	public PlayerRef getMyself() {
		return myself;
	}

	public GameRef getGame() {
		return game;
	}

	public List<PlayerRef> getPlayers() {
		return game.getPlayers();
	}
}
