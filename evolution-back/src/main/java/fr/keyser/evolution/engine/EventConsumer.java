package fr.keyser.evolution.engine;

public interface EventConsumer<E extends Event> {

	public void event(E event);
}
