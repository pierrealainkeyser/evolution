package fr.keyser.evolution.fsm.view;

import java.util.List;

public class UserView {

	private final int myself;

	private final List<CardView> hand;

	private final List<SummaryView> actions;

	public UserView(int myself, List<CardView> hand, List<SummaryView> actions) {
		this.myself = myself;
		this.hand = hand;
		this.actions = actions;
	}

	public int getMyself() {
		return myself;
	}

	public List<CardView> getHand() {
		return hand;
	}

	public List<SummaryView> getActions() {
		return actions;
	}
}
