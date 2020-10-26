package fr.keyser.fsm;

@FunctionalInterface
public interface TransitionHandler {

	public AutomatInstance handle(AutomatInstance instance, FollowedTransition transition);

	public default TransitionHandler then(TransitionHandler handler) {
		if (isNoop())
			return handler;

		return (a, t) -> handler.handle(handle(a, t), t);
	}

	default boolean isNoop() {
		return false;
	}

	public static class NoOp implements TransitionHandler {

		public final static NoOp INSTANCE = new NoOp();

		@Override
		public AutomatInstance handle(AutomatInstance instance, FollowedTransition transition) {
			return instance;
		}

		@Override
		public boolean isNoop() {
			return true;
		}

	}
}
