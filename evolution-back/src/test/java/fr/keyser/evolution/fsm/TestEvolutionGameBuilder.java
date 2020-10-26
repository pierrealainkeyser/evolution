package fr.keyser.evolution.fsm;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.Function;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.keyser.evolution.command.AddCardToPoolCommand;
import fr.keyser.evolution.command.AddTraitCommand;
import fr.keyser.evolution.command.FeedCommand;
import fr.keyser.evolution.core.DeckBuilder;
import fr.keyser.evolution.core.PlayArea;
import fr.keyser.evolution.core.Player;
import fr.keyser.evolution.core.Players;
import fr.keyser.evolution.engine.Event;
import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.Trait;
import fr.keyser.evolution.summary.FeedSummary;
import fr.keyser.evolution.summary.Summary;
import fr.keyser.fsm.AutomatInstance;
import fr.keyser.fsm.impl.AutomatEngine;
import fr.keyser.fsm.impl.NoTransitionFound;
import fr.keyser.fsm.impl.graph.AutomatGraph;

public class TestEvolutionGameBuilder {

	private static final Logger logger = LoggerFactory.getLogger(TestEvolutionGameBuilder.class);

	@Test
	void nominal() {

		DeckBuilder builder = new DeckBuilder();
		builder.create(Trait.AMBUSH, 4);
		builder.create(Trait.WARNING_CALL, 3);
		builder.create(Trait.BURROWING, 3);
		CardId longNeck = builder.create(Trait.LONGNECK, 7);
		builder.create(Trait.SYMBIOSIS, 3);
		builder.create(Trait.SYMBIOSIS, 1);
		builder.create(Trait.SYMBIOSIS, 2);
		builder.create(Trait.SYMBIOSIS, 4);
		builder.create(Trait.SYMBIOSIS, 6);
		builder.create(Trait.SYMBIOSIS, 3);
		builder.create(Trait.SYMBIOSIS, 2);
		builder.create(Trait.SYMBIOSIS, 4);
		builder.create(Trait.SYMBIOSIS, 6);
		builder.create(Trait.SYMBIOSIS, 3);
		builder.create(Trait.SYMBIOSIS, 2);
		builder.create(Trait.SYMBIOSIS, 4);
		builder.create(Trait.SYMBIOSIS, 6);
		builder.create(Trait.SYMBIOSIS, 3);

		PlayArea area = PlayArea.init(Players.players(2), builder.deck());

		EvolutionGameBuilder gbuilder = new EvolutionGameBuilder();
		AutomatGraph graph = gbuilder.build(2);

		AutomatEngine engine = AutomatEngine.start(graph, new PlayAreaMonitor(area));

		PlayAreaMonitor monitor = engine.get().getRoot().getGlobal(EvolutionGameBuilder.PLAY_AREA);
		Player p0 = monitor.getArea().getPlayer(0);
		CardId c0 = p0.getHands().get(0).getId();
		Player p1 = monitor.getArea().getPlayer(1);
		CardId c1 = p1.getHands().get(0).getId();

		PlayerBridge bridge0 = new PlayerBridge(0, engine);
		PlayerBridge bridge1 = new PlayerBridge(1, engine);

		for (Event e : monitor.getCurrents()) {
			logger.info("{}", e);
		}

		run(bridge0, b -> b.selectFood(new AddCardToPoolCommand(c0)));
		run(bridge1, b -> b.selectFood(new AddCardToPoolCommand(c1)));

		try {
			run(bridge1, PlayerBridge::pass);
			Assertions.failBecauseExceptionWasNotThrown(NoTransitionFound.class);
		} catch (NoTransitionFound ile) {
			// noop
		}

		run(bridge0, b -> b.playCard(new AddTraitCommand(longNeck, new SpecieId(0, 0), 0)));
		run(bridge0, PlayerBridge::pass);

		run(bridge1, PlayerBridge::pass);

		AutomatInstance second = engine.get().getRoot().getChilds().get(1);
		List<Summary> summary = second.getLocal(EvolutionGameBuilder.SUMMARY);
		assertThat(summary)
				.hasSize(1)
				.anySatisfy(s -> {
					assertThat(s).isInstanceOf(FeedSummary.class);
				});
		
		run(bridge1, b->b.feed(new FeedCommand(new SpecieId(1, 1))));
		
		for (AutomatInstance ai : engine.get().getAll()) {
			logger.info("{} {}", ai.getId(), ai.getCurrent());
		}

	}

	private void run(PlayerBridge bridge, Function<PlayerBridge, List<Event>> call) {
		List<Event> events = call.apply(bridge);
		logger.info("---");
		for (Event e : events) {
			logger.info("{}", e);
		}
	}
}
