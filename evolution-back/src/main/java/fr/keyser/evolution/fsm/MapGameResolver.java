package fr.keyser.evolution.fsm;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import fr.keyser.fsm.impl.AutomatEngine;

public class MapGameResolver implements GameResolver {

	private static class BoundGame {

		private final GameRef ref;

		private final AutomatEngine engine;

		public BoundGame(GameRef ref, AutomatEngine engine) {
			this.ref = ref;
			this.engine = engine;
		}

		public boolean match(String uuid) {
			return ref.getPlayers().stream().anyMatch(uuidMatch(uuid));
		}

		public ResolvedRef resolve(String uuid) {
			PlayerRef myself = ref.getPlayers().stream().filter(uuidMatch(uuid)).findFirst().get();
			return new ResolvedRef(myself, ref);
		}

		private Predicate<? super PlayerRef> uuidMatch(String uuid) {
			return p -> p.getUuid().equals(uuid);
		}

		public AutomatEngine getEngine() {
			return engine;
		}

	}

	private final Map<String, BoundGame> games = new ConcurrentHashMap<>();

	@Override
	public ResolvedRef findByUuid(String uuid) {
		Optional<ResolvedRef> findFirst = games.values().stream().filter(b -> b.match(uuid)).map(b -> b.resolve(uuid))
				.findFirst();
		return findFirst.orElse(null);

	}

	@Override
	public AutomatEngine getEngine(GameRef ref) {

		BoundGame boundGame = games.get(ref.getUuid());
		if (boundGame != null) {
			return boundGame.getEngine();
		}

		return null;
	}

	@Override
	public void addGame(ActiveGame active) {
		GameRef ref = active.getRef();
		games.put(ref.getUuid(), new BoundGame(ref, active.getEngine()));
	}

	@Override
	public void updateEngine(GameRef ref, AutomatEngine engine) {

	}
}
