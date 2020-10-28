package fr.keyser.evolution.fsm;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.Function;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.keyser.evolution.command.AddCardToPoolCommand;
import fr.keyser.evolution.command.AddTraitCommand;
import fr.keyser.evolution.command.FeedCommand;
import fr.keyser.evolution.core.DeckBuilder;
import fr.keyser.evolution.core.PlayArea;
import fr.keyser.evolution.core.Player;
import fr.keyser.evolution.core.Players;
import fr.keyser.evolution.engine.Event;
import fr.keyser.evolution.fsm.view.PartialRender;
import fr.keyser.evolution.fsm.view.Renderer;
import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.Trait;
import fr.keyser.evolution.summary.FeedSummary;
import fr.keyser.evolution.summary.Summary;
import fr.keyser.fsm.AutomatInstance;
import fr.keyser.fsm.impl.AutomatEngine;
import fr.keyser.fsm.impl.AutomatInstanceContainerValue;
import fr.keyser.fsm.impl.NoTransitionFound;
import fr.keyser.fsm.impl.graph.AutomatGraph;

public class TestEvolutionGraphBuilder {

	private static final Logger logger = LoggerFactory.getLogger(TestEvolutionGraphBuilder.class);

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

		EvolutionGraphBuilder gbuilder = new EvolutionGraphBuilder();
		AutomatGraph graph = gbuilder.build(2);

		AutomatEngine engine = AutomatEngine.start(graph, new PlayAreaMonitor(area));

		PlayAreaMonitor monitor = monitor(engine);
		Player p0 = monitor.getArea().getPlayer(0);
		CardId c0 = p0.getHands().get(0).getId();
		Player p1 = monitor.getArea().getPlayer(1);
		CardId c1 = p1.getHands().get(0).getId();

		assertDraw(engine, 0);
		PlayerBridge bridge0 = new PlayerBridge(new PlayerRef(0, null, null), engine);
		PlayerBridge bridge1 = new PlayerBridge(new PlayerRef(1, null, null), engine);

		for (Event e : monitor.getCurrents()) {
			logger.info("{}", e);
		}

		run(bridge0, b -> b.selectFood(new AddCardToPoolCommand(c0)));
		assertDraw(engine, 1);

		run(bridge1, b -> b.selectFood(new AddCardToPoolCommand(c1)));
		assertDraw(engine, 2);

		try {
			run(bridge1, PlayerBridge::pass);
			Assertions.failBecauseExceptionWasNotThrown(NoTransitionFound.class);
		} catch (NoTransitionFound ile) {
			// noop
		}
		assertDraw(engine, 2);

		run(bridge0, b -> b.playCard(new AddTraitCommand(longNeck, new SpecieId(0, 0), 0)));
		assertDraw(engine, 3);

		run(bridge0, PlayerBridge::pass);
		assertDraw(engine, 4);

		run(bridge1, PlayerBridge::pass);
		assertDraw(engine, 5);

		AutomatInstance second = engine.get().getRoot().getChilds().get(1);
		List<Summary> summary = second.getLocal(EvolutionGraphBuilder.SUMMARY);
		assertThat(summary)
				.hasSize(1)
				.anySatisfy(s -> {
					assertThat(s).isInstanceOf(FeedSummary.class);
				});

		run(bridge1, b -> b.feed(new FeedCommand(new SpecieId(1, 1))));
		assertDraw(engine, 6);

		for (AutomatInstance ai : engine.get().getAll()) {
			logger.info("{} {}", ai.getId(), ai.getCurrent());
		}

	}

	@Test
	void rendered() {

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

		EvolutionGraphBuilder gbuilder = new EvolutionGraphBuilder();
		AutomatGraph graph = gbuilder.build(2);

		AutomatEngine engine = AutomatEngine.start(graph, new PlayAreaMonitor(area));

		PlayAreaMonitor monitor = monitor(engine);
		Player p0 = monitor.getArea().getPlayer(0);
		CardId c0 = p0.getHands().get(0).getId();
		Player p1 = monitor.getArea().getPlayer(1);
		CardId c1 = p1.getHands().get(0).getId();

		assertDraw(engine, 0);
		PlayerBridge bridge0 = new PlayerBridge(new PlayerRef(0, null, null), engine);
		PlayerBridge bridge1 = new PlayerBridge(new PlayerRef(1, null, null), engine);

		render(bridge0, b -> b.selectFood(new AddCardToPoolCommand(c0)));
		assertDraw(engine, 1);

		render(bridge1, b -> b.selectFood(new AddCardToPoolCommand(c1)));
		assertDraw(engine, 2);

		try {
			render(bridge1, PlayerBridge::pass);
			Assertions.failBecauseExceptionWasNotThrown(NoTransitionFound.class);
		} catch (NoTransitionFound ile) {
			// noop
		}
		assertDraw(engine, 2);

		render(bridge0, b -> b.playCard(new AddTraitCommand(longNeck, new SpecieId(0, 0), 0)));
		assertDraw(engine, 3);

		render(bridge0, PlayerBridge::pass);
		assertDraw(engine, 4);

		render(bridge1, PlayerBridge::pass);
		assertDraw(engine, 5);

		AutomatInstance second = engine.get().getRoot().getChilds().get(1);
		List<Summary> summary = second.getLocal(EvolutionGraphBuilder.SUMMARY);
		assertThat(summary)
				.hasSize(1)
				.anySatisfy(s -> {
					assertThat(s).isInstanceOf(FeedSummary.class);
				});

		render(bridge1, b -> b.feed(new FeedCommand(new SpecieId(1, 1))));
		assertDraw(engine, 6);
		
//		AutomatInstanceContainerValue internal = engine.getInternal();
//		
//		try {
//			ObjectMapper om = new ObjectMapper();
//			logger.info("- internal -\n{}", om.writerWithDefaultPrettyPrinter().writeValueAsString(internal));
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
	}

	private static void assertDraw(AutomatEngine engine, int draw) {
		assertThat(monitor(engine).getDraw())
				.isEqualTo(draw);
	}

	private static PlayAreaMonitor monitor(AutomatEngine engine) {
		return engine.get().getRoot().getGlobal(EvolutionGraphBuilder.PLAY_AREA);
	}

	private void render(PlayerBridge bridge, Function<PlayerBridge, List<Event>> call) {
		Renderer renderer = new Renderer();

		List<Event> events = call.apply(bridge);
		PartialRender partial = renderer.partial(0, bridge.my(), events);
		try {
			ObjectMapper om = new ObjectMapper();
			logger.info("---\n{}", om.writerWithDefaultPrettyPrinter().writeValueAsString(partial));
		} catch (Exception e) {
			throw new RuntimeException(e);
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
