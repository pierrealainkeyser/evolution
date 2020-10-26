package fr.keyser.fsm.impl.fork;

import java.util.List;

import fr.keyser.fsm.AutomatInstance;
import fr.keyser.fsm.FollowedTransition;
import fr.keyser.fsm.InstanceId;
import fr.keyser.fsm.TransitionHandler;

public class ForkNamedTransitionHandler implements TransitionHandler {

	private final List<InstanceId> ids;

	public ForkNamedTransitionHandler(List<InstanceId> ids) {
		this.ids = ids;
	}

	@Override
	public AutomatInstance handle(AutomatInstance instance, FollowedTransition transition) {
		return instance.startChilds(ids, transition.getTo());
	}

}
