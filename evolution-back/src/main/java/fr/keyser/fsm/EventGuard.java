package fr.keyser.fsm;

@FunctionalInterface
public interface EventGuard {

	boolean accept(AutomatInstance instance, AutomatEvent event);
}
