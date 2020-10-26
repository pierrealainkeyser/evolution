package fr.keyser.fsm;

public interface FollowedTransitionVisitor {

	public default void visit(FollowedTransitionSource visited) {
		visited.accept(this);
		end();
	}

	public void direct(State state);

	public void guard(EventGuard guard);

	public void router();

	public void route(Object key);

	public void list();

	public void next();

	public void end();

}
