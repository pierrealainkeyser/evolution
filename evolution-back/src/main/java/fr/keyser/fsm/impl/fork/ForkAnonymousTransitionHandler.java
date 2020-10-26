package fr.keyser.fsm.impl.fork;

import fr.keyser.fsm.AutomatInstance;
import fr.keyser.fsm.FollowedTransition;
import fr.keyser.fsm.TransitionHandler;

public class ForkAnonymousTransitionHandler implements TransitionHandler {

	private final int number;

	public ForkAnonymousTransitionHandler(int number) {
		this.number = number;
	}

	@Override
	public AutomatInstance handle(AutomatInstance instance, FollowedTransition transition) {
		return instance.startChilds(number, transition.getTo());
	}
}
