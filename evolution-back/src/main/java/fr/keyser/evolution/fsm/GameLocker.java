package fr.keyser.evolution.fsm;

import java.util.function.Supplier;

@FunctionalInterface
public interface GameLocker {

	public <T> T withinLock(GameRef game, Supplier<T> supplier);

	public default void withinLock(GameRef game, Runnable runnable) {
		withinLock(game, () -> {
			runnable.run();
			return null;
		});
	}
}
