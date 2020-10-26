package fr.keyser.fsm.impl.choice;

import fr.keyser.fsm.AutomatInstance;
import fr.keyser.fsm.AutomatLifeCycleEvent;
import fr.keyser.fsm.FollowedTransition;
import fr.keyser.fsm.TransitionHandler;

public class ChoiceTransitionHandler implements TransitionHandler {

	public final static ChoiceTransitionHandler HANDLER = new ChoiceTransitionHandler();

	@Override
	public AutomatInstance handle(AutomatInstance instance, FollowedTransition transition) {
		return instance.unicast(AutomatLifeCycleEvent.CHOICE);
	}

}
