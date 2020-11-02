package fr.keyser.evolution.fsm;

import fr.keyser.evolution.summary.FeedingActionSummaries;

public class ActiveFeedingPlayer {

	private final int player;

	private final FeedingActionSummaries summary;

	public ActiveFeedingPlayer(int player, FeedingActionSummaries summary) {
		this.player = player;
		this.summary = summary;
	}

	public int getPlayer() {
		return player;
	}

	public FeedingActionSummaries getSummary() {
		return summary;
	}
}
