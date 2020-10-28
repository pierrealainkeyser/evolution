package fr.keyser.evolution.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class PlayerScoreBoard {

	private final int player;

	@JsonUnwrapped
	private final Score score;

	private final boolean alpha;

	public PlayerScoreBoard(int player, Score score, boolean alpha) {
		this.player = player;
		this.score = score;
		this.alpha = alpha;
	}

	public int getPlayer() {
		return player;
	}

	public Score getScore() {
		return score;
	}

	public boolean isAlpha() {
		return alpha;
	}
}
