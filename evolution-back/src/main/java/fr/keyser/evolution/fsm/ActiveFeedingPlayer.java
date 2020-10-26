package fr.keyser.evolution.fsm;

import java.util.List;

import fr.keyser.evolution.summary.Summary;

public class ActiveFeedingPlayer {

	private final int player;

	private final List<Summary> summary;

	public ActiveFeedingPlayer(int player, List<Summary> summary) {
		this.player = player;
		this.summary = summary;
	}

	public int getPlayer() {
		return player;
	}

	public List<Summary> getSummary() {
		return summary;
	}
}
