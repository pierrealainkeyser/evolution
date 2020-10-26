package fr.keyser.fsm.impl.rendezvous;

import java.util.List;
import java.util.function.Supplier;

import fr.keyser.fsm.AutomatInstance;
import fr.keyser.fsm.AutomatLifeCycleEvent;
import fr.keyser.fsm.FollowedTransition;
import fr.keyser.fsm.State;
import fr.keyser.fsm.TransitionHandler;

public class RendezVous {

	private final State child;

	private final TransitionHandler childEntryHandler = this::childHandle;

	private final State parent;

	private final TransitionHandler parentEntryHandler = this::parentHandle;

	public RendezVous(Supplier<State> parent, Supplier<State> child) {
		this.parent = parent.get();
		this.child = child.get();
	}

	public TransitionHandler getChildEntryHandler() {
		return childEntryHandler;
	}

	public TransitionHandler getParentEntryHandler() {
		return parentEntryHandler;
	}

	private AutomatInstance childHandle(AutomatInstance instance, FollowedTransition transition) {
		AutomatInstance parentInstance = instance.getParent().get();
		boolean parentOk = parent.equals(parentInstance.getCurrent());
		boolean transitionOk = child.equals(transition.getTo());

		List<AutomatInstance> childs = parentInstance.getChilds();
		long found = childs.stream().filter(ai -> child.equals(ai.getCurrent())).count();
		boolean otherOk = found == childs.size() - 1;
		if (parentOk && transitionOk && otherOk) {
			join(parentInstance);
		}
		return instance;

	}

	private AutomatInstance parentHandle(AutomatInstance instance, FollowedTransition transition) {

		// all state are matching
		boolean parentOk = parent.equals(transition.getTo());
		boolean allMatch = instance.getChilds().stream().allMatch(ai -> child.equals(ai.getCurrent()));
		if (parentOk && allMatch) {
			join(instance);
		}

		return instance;
	}

	private void join(AutomatInstance parent) {
		// send the join event
		parent.unicast(AutomatLifeCycleEvent.JOIN);
		parent.multicast(parent.getChildsId(), AutomatLifeCycleEvent.CHILD_JOIN);
	}

}
