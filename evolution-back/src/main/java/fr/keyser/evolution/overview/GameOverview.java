package fr.keyser.evolution.overview;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.keyser.evolution.model.Trait;

public class GameOverview {

	private final Instant created;

	private final String game;

	private final String user;

	private final String playerUUID;

	private final boolean quickplay;

	private final List<Trait> traits;

	private final List<String> players;

	private final boolean terminated;

	private final Integer score;

	private final Boolean alpha;

	public GameOverview(Instant created, String game, String user, String playerUUID, boolean quickplay,
			List<Trait> traits,
			List<String> players, boolean terminated, Integer score, Boolean alpha) {
		this.created = created;
		this.user = user;
		this.game = game;
		this.playerUUID = playerUUID;
		this.quickplay = quickplay;
		this.traits = traits;
		this.players = players;
		this.terminated = terminated;
		this.score = score;
		this.alpha = alpha;
	}

	@JsonIgnore
	public String getUser() {
		return user;
	}

	public String getGame() {
		return game;
	}

	public String getPlayerUUID() {
		return playerUUID;
	}

	public boolean isQuickplay() {
		return quickplay;
	}

	public List<Trait> getTraits() {
		return traits;
	}

	public List<String> getPlayers() {
		return players;
	}

	public boolean isTerminated() {
		return terminated;
	}

	public Integer getScore() {
		return score;
	}

	public Boolean getAlpha() {
		return alpha;
	}

	public Instant getCreated() {
		return created;
	}
}
