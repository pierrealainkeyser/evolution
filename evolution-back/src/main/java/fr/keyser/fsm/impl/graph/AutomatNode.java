package fr.keyser.fsm.impl.graph;

import fr.keyser.fsm.FollowedTransitionSource;
import fr.keyser.fsm.State;
import fr.keyser.fsm.TransitionHandler;

public class AutomatNode {

	private final State state;

	private final String type;

	private final FollowedTransitionSource transitionSource;

	private final boolean requireValidTransition;

	private final TransitionHandler entry;

	private final TransitionHandler leave;

	public AutomatNode(State state, String type, boolean requireValidTransition,
			FollowedTransitionSource transitionSource, TransitionHandler entry,
			TransitionHandler leave) {
		this.state = state;
		this.type = type;
		this.requireValidTransition = requireValidTransition;
		this.transitionSource = transitionSource;
		this.entry = entry;
		this.leave = leave;
	}

	public State getState() {
		return state;
	}

	public String getType() {
		return type;
	}

	public FollowedTransitionSource getTransitionSource() {
		return transitionSource;
	}

	public TransitionHandler getEntry() {
		return entry;
	}

	public TransitionHandler getLeave() {
		return leave;
	}

	public boolean isRequireValidTransition() {
		return requireValidTransition;
	}
}
