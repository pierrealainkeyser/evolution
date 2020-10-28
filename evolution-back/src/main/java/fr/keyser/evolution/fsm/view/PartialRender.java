package fr.keyser.evolution.fsm.view;

import java.util.List;

import fr.keyser.evolution.model.PlayerScoreBoard;

public class PartialRender extends Render {

	private final List<RenderedEvent> events;

	private final List<PlayerScoreBoard> scoreBoards;

	private final List<SummaryView> actions;

	public PartialRender(int draw, List<SummaryView> actions, List<PlayerScoreBoard> scoreBoards,
			List<RenderedEvent> events) {
		super(draw);
		this.actions = actions;
		this.scoreBoards = scoreBoards;
		this.events = events;
	}

	@Override
	public String getType() {
		return "partial";
	}

	public List<RenderedEvent> getEvents() {
		return events;
	}

	public List<SummaryView> getActions() {
		return actions;
	}

	public List<PlayerScoreBoard> getScoreBoards() {
		return scoreBoards;
	}
}
