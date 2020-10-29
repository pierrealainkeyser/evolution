package fr.keyser.evolution.fsm;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.keyser.evolution.core.Deck;
import fr.keyser.evolution.core.PlayArea;
import fr.keyser.evolution.core.Players;
import fr.keyser.evolution.model.EvolutionGameSettings;
import fr.keyser.evolution.model.TraitsPackCollections;
import fr.keyser.fsm.impl.AutomatEngine;
import fr.keyser.fsm.impl.graph.AutomatGraph;

public class GameBuilder {

	private final TraitsPackCollections traitsPack = TraitsPackCollections.createDefault();

	private final EvolutionGraphBuilder graphBuilder = new EvolutionGraphBuilder();

	public ActiveGame create(EvolutionGameSettings settings) {
		int playersCount = settings.getPlayersCount();
		List<PlayerRef> prefs = new ArrayList<>();
		for (int i = 0; i < playersCount; ++i) {
			prefs.add(new PlayerRef(i, newUUID(), settings.getPlayers().get(i)));
		}

		GameRef ref = new GameRef(newUUID(), prefs);

		Deck deck = traitsPack.create(settings);
		PlayArea area = PlayArea.init(Players.players(playersCount), deck);

		AutomatGraph logic = graphBuilder.build(settings);
		AutomatEngine engine = AutomatEngine.start(logic, new PlayAreaMonitor(area));

		return new ActiveGame(ref, settings, engine);
	}

	private String newUUID() {
		return UUID.randomUUID().toString();
	}
}
