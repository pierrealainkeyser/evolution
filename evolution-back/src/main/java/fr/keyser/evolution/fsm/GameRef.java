package fr.keyser.evolution.fsm;

import java.util.List;

public class GameRef {

	private final String uuid;

	private final List<PlayerRef> players;

	public GameRef(String uuid, List<PlayerRef> players) {
		this.uuid = uuid;
		this.players = players;
	}

	public int getPlayersCount() {
		return players.size();
	}

	public String getUuid() {
		return uuid;
	}

	public List<PlayerRef> getPlayers() {
		return players;
	}

}
