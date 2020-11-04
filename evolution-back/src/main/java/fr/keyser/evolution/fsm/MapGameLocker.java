package fr.keyser.evolution.fsm;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class MapGameLocker implements GameLocker {

	private final ConcurrentMap<String, Object> map = new ConcurrentHashMap<>();

	@Override
	public <T> T withinLock(GameRef game, Supplier<T> supplier) {
		AtomicReference<T> ref = new AtomicReference<>();

		map.compute(game.getUuid(), (k, v) -> {
			ref.set(supplier.get());
			return null;
		});

		return ref.get();
	}

}
