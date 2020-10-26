package fr.keyser.fsm;

@FunctionalInterface
public interface FollowedTransitionSource {

	FollowedTransition findTransition(AutomatInstance instance, AutomatEvent event);

	/**
	 * Visit the transitions
	 * 
	 * @param visitor
	 */
	default void accept(FollowedTransitionVisitor visitor) {

	}

	default boolean isNoop() {
		return false;
	}

	public static class NoOp implements FollowedTransitionSource {

		public final static NoOp INSTANCE = new NoOp();

		@Override
		public FollowedTransition findTransition(AutomatInstance instance, AutomatEvent event) {
			return null;
		}

		@Override
		public boolean isNoop() {
			return true;
		}

	}
}
