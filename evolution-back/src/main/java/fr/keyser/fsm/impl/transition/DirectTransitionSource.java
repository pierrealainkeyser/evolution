package fr.keyser.fsm.impl.transition;

import java.util.function.Supplier;

import fr.keyser.fsm.AutomatEvent;
import fr.keyser.fsm.AutomatInstance;
import fr.keyser.fsm.FollowedTransition;
import fr.keyser.fsm.FollowedTransitionSource;
import fr.keyser.fsm.FollowedTransitionVisitor;
import fr.keyser.fsm.State;

public class DirectTransitionSource implements FollowedTransitionSource {

	private final State destination;

	public DirectTransitionSource(Supplier<State> destination) {
		this.destination = destination.get();
	}

	@Override
	public FollowedTransition findTransition(AutomatInstance instance, AutomatEvent event) {
		return new FollowedTransition(instance.getCurrent(), event, destination);
	}

	@Override
	public void accept(FollowedTransitionVisitor visitor) {
		visitor.direct(destination);
	}

}
