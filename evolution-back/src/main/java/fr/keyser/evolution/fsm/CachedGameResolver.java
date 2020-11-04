package fr.keyser.evolution.fsm;

import java.util.LinkedHashMap;
import java.util.Map;

import fr.keyser.fsm.impl.AutomatEngine;
import fr.keyser.security.AuthenticatedPlayer;

public class CachedGameResolver implements GameResolver {

	private final GameResolver delegated;

	private final Map<String, ResolvedRef> cacheRef = new LinkedHashMap<>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(java.util.Map.Entry<String, ResolvedRef> eldest) {
			return size() > 20;
		}
	};

	private final Map<String, ActiveGame> cacheGame = new LinkedHashMap<>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(java.util.Map.Entry<String, ActiveGame> eldest) {
			return size() > 20;
		}
	};

	public CachedGameResolver(GameResolver delegated) {
		this.delegated = delegated;
	}

	@Override
	public ResolvedRef findByUuid(String uuid) {
		return cacheRef.compute(uuid, (key, input) -> {
			if (input == null)
				input = delegated.findByUuid(key);
			return input;
		});
	}

	@Override
	public AutomatEngine getEngine(GameRef ref) {
		ActiveGame activeGame = cacheGame.get(ref.getUuid());
		if (activeGame == null)
			return delegated.getEngine(ref);
		else
			return activeGame.getEngine();

	}

	@Override
	public void updateEngine(GameRef ref, AutomatEngine engine) {
		delegated.updateEngine(ref, engine);
		cacheGame.compute(ref.getUuid(), (key, input) -> {
			if (input == null)
				input = new ActiveGame(ref, null, engine);
			else
				input = input.withEngine(engine);
			return input;
		});

	}

	@Override
	public void addGame(ActiveGame active, AuthenticatedPlayer owner) {
		delegated.addGame(active, owner);
		cacheGame.put(active.getRef().getUuid(), active);

	}
}
