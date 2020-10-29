package fr.keyser.evolution.fsm.view;

import java.util.List;

public class CompleteRender extends Render {

	private final PlayerAreaView game;

	private final UserView user;

	private final List<RenderedEvent> events;

	public CompleteRender(int draw, UserView user, PlayerAreaView game, List<RenderedEvent> events) {
		super(draw);
		this.user = user;
		this.game = game;
		this.events = events;
	}

	@Override
	public String getType() {
		return "complete";
	}

	public PlayerAreaView getGame() {
		return game;
	}

	public UserView getUser() {
		return user;
	}

	public List<RenderedEvent> getEvents() {
		return events;
	}
}
