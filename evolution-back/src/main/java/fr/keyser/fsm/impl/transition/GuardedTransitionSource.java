package fr.keyser.fsm.impl.transition;

import fr.keyser.fsm.AutomatEvent;
import fr.keyser.fsm.AutomatInstance;
import fr.keyser.fsm.EventGuard;
import fr.keyser.fsm.FollowedTransition;
import fr.keyser.fsm.FollowedTransitionSource;
import fr.keyser.fsm.FollowedTransitionVisitor;

public class GuardedTransitionSource implements FollowedTransitionSource {

	private final FollowedTransitionSource delegated;

	private final EventGuard guard;

	public GuardedTransitionSource(EventGuard guard, FollowedTransitionSource delegated) {
		this.guard = guard;
		this.delegated = delegated;
	}

	@Override
	public FollowedTransition findTransition(AutomatInstance instance, AutomatEvent event) {

		if (guard.accept(instance, event)) {
			return delegated.findTransition(instance, event);
		}

		return null;
	}

	@Override
	public void accept(FollowedTransitionVisitor visitor) {
		visitor.guard(guard);
		visitor.visit(delegated);
	}

}
