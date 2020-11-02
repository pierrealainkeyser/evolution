package fr.keyser.evolution.fsm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.keyser.evolution.core.Deck;
import fr.keyser.evolution.core.PlayArea;
import fr.keyser.evolution.core.Players;
import fr.keyser.evolution.model.EvolutionGameParameters;
import fr.keyser.evolution.model.EvolutionGameSettings;
import fr.keyser.evolution.model.TraitsPackCollections;
import fr.keyser.fsm.AutomatLogic;
import fr.keyser.fsm.impl.AutomatEngine;

public class GameBuilder {

	private final TraitsPackCollections traitsPack = TraitsPackCollections.createDefault();

	private final EvolutionGraphBuilder graphBuilder = new EvolutionGraphBuilder();

	private final Map<EvolutionGameParameters, AutomatLogic> logics = new LinkedHashMap<>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1958193881993890326L;

		protected boolean removeEldestEntry(Map.Entry<EvolutionGameParameters, AutomatLogic> eldest) {
			return size() > 10;

		};
	};

	public ActiveGame create(EvolutionGameSettings settings) {
		return create(settings, traitsPack.create(settings));
	}

	ActiveGame create(EvolutionGameSettings settings, Deck deck) {
		GameRef ref = newRef(settings);

		PlayArea area = PlayArea.init(Players.players(ref.getPlayersCount()), deck);
		EvolutionGameParameters parameters = settings.getParameters();
		AutomatLogic logic = createLogic(parameters);
		AutomatEngine engine = AutomatEngine.start(logic, new PlayAreaMonitor(area));

		return new ActiveGame(ref, settings, engine);
	}

	private GameRef newRef(EvolutionGameSettings settings) {
		int playersCount = settings.getPlayersCount();
		List<PlayerRef> prefs = new ArrayList<>();
		for (int i = 0; i < playersCount; ++i)
			prefs.add(new PlayerRef(i, newUUID(), settings.getPlayers().get(i)));

		return new GameRef(newUUID(), prefs);
	}

	private String newUUID() {
		return UUID.randomUUID().toString();
	}

	public AutomatLogic createLogic(EvolutionGameParameters parameters) {
		return logics.compute(parameters, (key, input) -> {
			if (input != null)
				return input;
			else
				return graphBuilder.build(key);
		});
	}

	public boolean isTerminated(AutomatEngine engine) {
		return graphBuilder.isTerminated(engine);
	}
}
