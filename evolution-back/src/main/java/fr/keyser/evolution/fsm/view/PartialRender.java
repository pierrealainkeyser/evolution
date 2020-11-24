package fr.keyser.evolution.fsm.view;

import java.util.List;

import fr.keyser.evolution.model.PlayerScoreBoard;

public class PartialRender extends Render {

	private final List<RenderedEvent> events;

	private final List<PlayerScoreBoard> scoreBoards;

	private final ActionsView actions;

	public PartialRender(int draw, ActionsView actions, List<PlayerScoreBoard> scoreBoards,
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

	public ActionsView getActions() {
		return actions;
	}

	public List<PlayerScoreBoard> getScoreBoards() {
		return scoreBoards;
	}
}
