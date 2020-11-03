package fr.keyser.evolution.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class PlayerScoreBoard {

	private final int player;

	@JsonIgnore
	private final Score score;

	private final boolean alpha;

	@JsonCreator
	public PlayerScoreBoard(@JsonProperty("player") int player, @JsonProperty("food") int food,
			@JsonProperty("traits") int traits, @JsonProperty("population") int population,
			@JsonProperty("alpha") boolean alpha) {
		this(player, new Score(food, traits, population), alpha);
	}

	public PlayerScoreBoard(int player, Score score, boolean alpha) {
		this.player = player;
		this.score = score;
		this.alpha = alpha;
	}

	public int getPlayer() {
		return player;
	}

	@JsonUnwrapped
	public Score getScore() {
		return score;
	}

	public boolean isAlpha() {
		return alpha;
	}
}
