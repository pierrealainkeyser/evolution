package fr.keyser.fsm;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class FollowedTransition {

	private final AutomatEvent event;

	private final State from;

	private final State to;

	private final boolean reentrant;

	public FollowedTransition(State from, AutomatEvent event, State to) {
		this.from = from;
		this.event = event;
		this.to = to;
		this.reentrant = from.equals(to);
	}

	public FollowedTransition map(UnaryOperator<State> toMapper) {
		return new FollowedTransition(from, event, toMapper.apply(to));
	}

	public Stream<State> leaving() {
		if (reentrant)
			return Stream.of(from);

		return from.diff(to, true);
	}

	public Stream<State> entering() {
		if (reentrant)
			return Stream.of(to);

		return to.diff(from, false);
	}

	public State getFrom() {
		return from;
	}

	public Object getKey() {
		return event.getKey();
	}

	public Object getPayload() {
		return event.getPayload();
	}

	public State getTo() {
		return to;
	}

	@Override
	public String toString() {
		return String.format("FollowedTransition [from=%s, event=%s, to=%s]", from, event, to);
	}

}
