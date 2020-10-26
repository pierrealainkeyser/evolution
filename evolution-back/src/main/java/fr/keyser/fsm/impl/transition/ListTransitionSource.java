package fr.keyser.fsm.impl.transition;

import java.util.List;

import fr.keyser.fsm.AutomatEvent;
import fr.keyser.fsm.AutomatInstance;
import fr.keyser.fsm.FollowedTransition;
import fr.keyser.fsm.FollowedTransitionSource;
import fr.keyser.fsm.FollowedTransitionVisitor;

public class ListTransitionSource implements FollowedTransitionSource {

	private final List<? extends FollowedTransitionSource> delegated;

	public ListTransitionSource(List<? extends FollowedTransitionSource> delegated) {
		this.delegated = delegated;
	}

	@Override
	public FollowedTransition findTransition(AutomatInstance instance, AutomatEvent event) {
		for (FollowedTransitionSource s : delegated) {
			FollowedTransition found = s.findTransition(instance, event);
			if (found != null)
				return found;
		}
		return null;
	}

	@Override
	public void accept(FollowedTransitionVisitor visitor) {

		visitor.list();
		for (FollowedTransitionSource f : delegated) {
			visitor.next();
			visitor.visit(f);
		}
	}

}
