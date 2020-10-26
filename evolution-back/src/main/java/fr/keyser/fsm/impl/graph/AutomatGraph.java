package fr.keyser.fsm.impl.graph;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import fr.keyser.fsm.AutomatEvent;
import fr.keyser.fsm.AutomatInstance;
import fr.keyser.fsm.AutomatLogic;
import fr.keyser.fsm.FollowedTransition;
import fr.keyser.fsm.State;
import fr.keyser.fsm.impl.NoTransitionFound;

/**
 * Use a Map of {@link AutomatNode} to performs the {@link AutomatLogic}
 * 
 * @author pakeyser
 *
 */
public class AutomatGraph implements AutomatLogic {

	private final Map<State, AutomatNode> nodes;

	private final Map<State, State> toFirstState;

	public AutomatGraph(List<AutomatNode> nodes, Map<State, State> toFirstState) {
		this.nodes = nodes.stream().collect(Collectors.toMap(AutomatNode::getState, Function.identity()));
		this.toFirstState = toFirstState;
	}

	@Override
	public FollowedTransition findTransition(AutomatInstance instance, AutomatEvent event) {

		State state = instance.getCurrent();
		AutomatNode node = nodes.get(state);
		if (node != null) {
			FollowedTransition findTransition = node.getTransitionSource().findTransition(instance, event);
			if (findTransition != null) {
				// lookup the effective state
				return findTransition.map(toFirstState::get);
			} else {
				if (node.isRequireValidTransition()) {
					throw new NoTransitionFound(state, event);
				}
			}

		}

		return null;
	}

	@Override
	public AutomatInstance leaving(AutomatInstance leaving, State leaved, FollowedTransition transition) {
		AutomatNode node = nodes.get(leaved);
		if (node != null) {
			return node.getLeave().handle(leaving, transition);
		}
		return leaving;
	}

	@Override
	public AutomatInstance entering(AutomatInstance entering, State entered, FollowedTransition transition) {
		AutomatNode node = nodes.get(entered);
		if (node != null) {
			return node.getEntry().handle(entering, transition);
		}
		return entering;
	}
}
