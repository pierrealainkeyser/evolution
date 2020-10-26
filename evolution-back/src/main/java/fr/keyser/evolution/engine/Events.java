package fr.keyser.evolution.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Events<E extends Event, P extends EventProcessor<E, P>> {

	private final List<Event> events;

	private final List<E> working = new ArrayList<>();

	private final P output;

	Events(P input, List<? extends E> first) {
		this(input, new ArrayList<>(), first);
	}

	public Events<E, P> and(List<? extends E> inputs) {
		return new Events<>(output, events, inputs);
	}

	private Events(P input, List<Event> events, List<? extends E> first) {
		this.events = events;
		first.forEach(this::addEvent);

		P current = input;
		while (!working.isEmpty()) {
			E e = working.remove(0);
			current = current.process(this::addEvent, e);

		}
		this.output = current;
	}

	private void addEvent(E event) {
		events.add(event);
		working.add(event);
	}

	public List<Event> getEvents() {
		return Collections.unmodifiableList(events);
	}

	public P getOutput() {
		return output;
	}

}
