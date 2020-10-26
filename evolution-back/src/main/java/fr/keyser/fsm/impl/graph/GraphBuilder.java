package fr.keyser.fsm.impl.graph;

import static fr.keyser.fsm.impl.transition.TransitionSourceBuilder.to;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import fr.keyser.fsm.AutomatInstance;
import fr.keyser.fsm.AutomatLifeCycle;
import fr.keyser.fsm.FollowedTransitionSource;
import fr.keyser.fsm.InstanceId;
import fr.keyser.fsm.State;
import fr.keyser.fsm.TransitionHandler;
import fr.keyser.fsm.impl.choice.ChoiceTransitionHandler;
import fr.keyser.fsm.impl.fork.ForkAnonymousTransitionHandler;
import fr.keyser.fsm.impl.fork.ForkNamedTransitionHandler;
import fr.keyser.fsm.impl.rendezvous.RendezVous;
import fr.keyser.fsm.impl.transition.TransitionSourceBuilder;
import fr.keyser.fsm.impl.transition.TransitionSourceBuilder.ChoiceBuilder;

public class GraphBuilder {

	public class NodeBuilder implements Supplier<State> {
		private TransitionHandler entry = TransitionHandler.NoOp.INSTANCE;

		private TransitionHandler leave = TransitionHandler.NoOp.INSTANCE;

		private final State state;

		private FollowedTransitionSource transitionSource = FollowedTransitionSource.NoOp.INSTANCE;

		private NodeBuilder firstChild;

		private boolean requireValidTransition = true;

		private NodeBuilder(State state) {
			this.state = state;
			nodes.put(state, this);
		}

		public NodeBuilder requireValidTransition(boolean requireValidTransition) {
			this.requireValidTransition = requireValidTransition;
			return this;
		}

		public State resolveFirstState() {
			if (firstChild == null)
				return state;
			else
				return firstChild.resolveFirstState();
		}

		public AutomatNode build() {
			return new AutomatNode(state, null, requireValidTransition, transitionSource, entry, leave);
		}

		public NodeBuilder entry(UnaryOperator<AutomatInstance> entry) {
			return entry(wrap(entry));
		}

		public TransitionHandler wrap(UnaryOperator<AutomatInstance> entry) {
			return (ai, t) -> entry.apply(ai);
		}

		public NodeBuilder entry(TransitionHandler entry) {
			this.entry = this.entry.then(entry);
			return this;
		}

		@Override
		public State get() {
			return state;
		}

		public void choice(Function<ChoiceBuilder, FollowedTransitionSource> func) {
			transition(func.apply(new ChoiceBuilder()));
			entry(ChoiceTransitionHandler.HANDLER);
		}

		public void auto(NodeBuilder to) {
			choice(c -> c.orElse(to(to)));
		}

		public void fork(NodeBuilder parent, int childs, NodeBuilder child) {
			entry(new ForkAnonymousTransitionHandler(childs));
			transition(TransitionSourceBuilder.fork(to(parent), to(child)));
		}

		public void fork(NodeBuilder parent, List<InstanceId> childIds, NodeBuilder child) {
			entry(new ForkNamedTransitionHandler(childIds));
			transition(TransitionSourceBuilder.fork(to(parent), to(child)));
		}

		public NodeBuilder leave(UnaryOperator<AutomatInstance> leave) {
			return leave(wrap(leave));
		}

		public NodeBuilder leave(TransitionHandler leave) {
			this.leave = this.leave.then(leave);
			return this;
		}

		public NodeBuilder sub(String state) {

			NodeBuilder nodeBuilder = new NodeBuilder(this.state.sub(state));
			if (firstChild == null) {
				firstChild = nodeBuilder;
			}

			return nodeBuilder;
		}

		public NodeBuilder transition(Supplier<FollowedTransitionSource> transitionSource) {
			return transition(transitionSource.get());
		}

		public NodeBuilder transition(FollowedTransitionSource transitionSource) {
			this.transitionSource = transitionSource;
			return this;
		}

		public GraphBuilder graph() {
			return GraphBuilder.this;
		}
	}

	private Map<State, NodeBuilder> nodes = new LinkedHashMap<>();

	public GraphBuilder() {
		root(AutomatLifeCycle.INITIAL);
	}

	public GraphBuilder rendezVous(NodeBuilder parent, NodeBuilder child) {
		RendezVous rdv = new RendezVous(parent, child);
		parent.entry(rdv.getParentEntryHandler());
		child.entry(rdv.getChildEntryHandler());
		return this;
	}

	public NodeBuilder initial() {
		return get(AutomatLifeCycle.INITIAL);
	}

	public NodeBuilder get(State key) {
		return nodes.get(key);
	}

	public NodeBuilder root(String state) {
		return root(new State(state));
	}

	private NodeBuilder root(State state) {
		return new NodeBuilder(state);
	}

	public AutomatGraph build() {
		Collection<NodeBuilder> nodesList = nodes.values();
		Map<State, State> toFirstState = nodesList.stream()
				.collect(Collectors.toMap(NodeBuilder::get, NodeBuilder::resolveFirstState));

		return new AutomatGraph(nodesList.stream().map(NodeBuilder::build).collect(Collectors.toList()), toFirstState);
	}
}
