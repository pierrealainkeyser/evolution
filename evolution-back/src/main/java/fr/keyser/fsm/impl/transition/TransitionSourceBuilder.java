package fr.keyser.fsm.impl.transition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import fr.keyser.fsm.AutomatLifeCycleEvent;
import fr.keyser.fsm.EventGuard;
import fr.keyser.fsm.FollowedTransitionSource;
import fr.keyser.fsm.State;

public class TransitionSourceBuilder {

	public static ChoiceBuilder choice() {
		return new ChoiceBuilder();
	}

	public static GuardBuilder guard() {
		return new GuardBuilder();
	}

	public static FollowedTransitionSource fork(FollowedTransitionSource parent, FollowedTransitionSource children) {

		return route() //
				.on(AutomatLifeCycleEvent.START, children) //
				.on(AutomatLifeCycleEvent.CHILDS_STARTED, parent) //
				.get();

	}

	public static FollowedTransitionSource join(FollowedTransitionSource parent, FollowedTransitionSource children) {
		return route() //
				.on(AutomatLifeCycleEvent.JOIN, parent) //
				.on(AutomatLifeCycleEvent.CHILD_JOIN, children) //
				.get();
	}

	public static FollowedTransitionSource join(FollowedTransitionSource destination) {
		return join(destination, destination);
	}

	public static FollowedTransitionSource check(EventGuard guard, FollowedTransitionSource destination) {
		return guard().check(guard, destination).get();
	}

	public static FollowedTransitionSource to(Supplier<State> destination) {
		return new DirectTransitionSource(destination);
	}

	public static RouteBuilder route() {
		return new RouteBuilder();
	}

	public static class RouteBuilder implements Supplier<FollowedTransitionSource> {
		private Map<Object, FollowedTransitionSource> sources = new HashMap<>();

		public RouteBuilder on(Object key, Supplier<FollowedTransitionSource> delegated) {
			return on(key, delegated.get());
		}

		public RouteBuilder on(Object key, FollowedTransitionSource delegated) {
			sources.put(key, delegated);
			return this;
		}

		@Override
		public FollowedTransitionSource get() {
			return new RouteTransitionSource(sources);
		}

	}

	public static class ChoiceBuilder {

		private final List<FollowedTransitionSource> sources = new ArrayList<>();

		public ChoiceBuilder when(EventGuard guard, FollowedTransitionSource delegated) {
			this.sources.add(new GuardedTransitionSource(guard, delegated));
			return this;
		}

		public FollowedTransitionSource orElse(FollowedTransitionSource defaultSource) {
			List<FollowedTransitionSource> newSources = new ArrayList<>(this.sources.size() + 1);
			newSources.addAll(this.sources);
			newSources.add(defaultSource);

			return route()
					.on(AutomatLifeCycleEvent.CHOICE, new ListTransitionSource(newSources))
					.get();

		}
	}

	public static class GuardBuilder implements Supplier<FollowedTransitionSource> {

		private List<GuardedTransitionSource> sources = new ArrayList<>();

		public GuardBuilder check(EventGuard guard, Supplier<FollowedTransitionSource> delegated) {
			return check(guard, delegated.get());
		}

		public GuardBuilder check(EventGuard guard, FollowedTransitionSource delegated) {
			this.sources.add(new GuardedTransitionSource(guard, delegated));
			return this;
		}

		@Override
		public FollowedTransitionSource get() {
			if (sources.size() == 1) {
				return sources.get(0);
			} else
				return new ListTransitionSource(sources);
		}
	}
}
