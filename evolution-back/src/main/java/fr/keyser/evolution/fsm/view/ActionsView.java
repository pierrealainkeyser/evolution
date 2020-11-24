package fr.keyser.evolution.fsm.view;

import java.util.List;

public class ActionsView {

	private final List<SummaryView> actions;

	private final boolean pass;

	public ActionsView(List<SummaryView> actions, boolean pass) {
		this.actions = actions;
		this.pass = pass;
	}

	public List<SummaryView> getActions() {
		return actions;
	}

	public boolean isPass() {
		return pass;
	}

}
