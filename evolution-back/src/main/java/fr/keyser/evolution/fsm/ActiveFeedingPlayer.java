package fr.keyser.evolution.fsm;

import java.util.List;

import fr.keyser.evolution.summary.FeedingActionSummary;

public class ActiveFeedingPlayer {

	private final int player;

	private final List<FeedingActionSummary> summary;

	public ActiveFeedingPlayer(int player, List<FeedingActionSummary> summary) {
		this.player = player;
		this.summary = summary;
	}

	public int getPlayer() {
		return player;
	}

	public List<FeedingActionSummary> getSummary() {
		return summary;
	}
}
