package fr.keyser.fsm.impl.transition;

import java.util.Map;
import java.util.Map.Entry;

import fr.keyser.fsm.AutomatEvent;
import fr.keyser.fsm.AutomatInstance;
import fr.keyser.fsm.FollowedTransition;
import fr.keyser.fsm.FollowedTransitionSource;
import fr.keyser.fsm.FollowedTransitionVisitor;

public class RouteTransitionSource implements FollowedTransitionSource {

	private final Map<Object, FollowedTransitionSource> delegated;

	public RouteTransitionSource(Map<Object, FollowedTransitionSource> delegated) {
		this.delegated = delegated;
	}

	@Override
	public FollowedTransition findTransition(AutomatInstance instance, AutomatEvent event) {

		Object key = event.getKey();
		FollowedTransitionSource found = delegated.get(key);
		if (found != null)
			return found.findTransition(instance, event);

		return null;
	}

	@Override
	public void accept(FollowedTransitionVisitor visitor) {

		visitor.router();
		for (Entry<Object, FollowedTransitionSource> entry : delegated.entrySet()) {
			visitor.next();			
			visitor.route(entry.getKey());
			visitor.visit(entry.getValue());
		}
	}

}
