package fr.keyser.evolution.fsm;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
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
import fr.keyser.evolution.core.PlayerState;
import fr.keyser.evolution.core.Players;
import fr.keyser.evolution.engine.Event;
import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.EvolutionGameSettings;
import fr.keyser.evolution.model.PlayerInputStatus;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.Trait;
import fr.keyser.evolution.summary.FeedSummary;
import fr.keyser.evolution.summary.FeedingActionSummary;
import fr.keyser.fsm.AutomatInstance;
import fr.keyser.fsm.impl.AutomatEngine;
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

		PlayArea area = PlayArea.init(Players.players(2), builder.deck());

		AuthenticatedPlayer ap0 = new AuthenticatedPlayer("p1", "Joueur 1");
		AuthenticatedPlayer ap1 = new AuthenticatedPlayer("p2", "Joueur 2");

		EvolutionGraphBuilder gbuilder = new EvolutionGraphBuilder();
		AutomatGraph graph = gbuilder.build(new EvolutionGameSettings(Arrays.asList(ap0, ap1), false));

		AutomatEngine engine = AutomatEngine.start(graph, new PlayAreaMonitor(area));

		PlayAreaMonitor monitor = monitor(engine);
		Player p0 = monitor.getArea().getPlayer(0);
		CardId c0 = p0.getHands().get(0).getId();
		Player p1 = monitor.getArea().getPlayer(1);
		CardId c1 = p1.getHands().get(0).getId();

		assertDraw(engine, 0);
		PlayerBridge bridge0 = new PlayerBridge(new PlayerRef(0, null, ap0), engine);
		PlayerBridge bridge1 = new PlayerBridge(new PlayerRef(1, null, ap1), engine);

		for (Event e : monitor.getCurrents()) {
			logger.info("{}", e);
		}

		run(bridge0, b -> b.selectFood(new AddCardToPoolCommand(c0)));
		assertDraw(engine, 1);

		run(bridge1, b -> b.selectFood(new AddCardToPoolCommand(c1)));
		assertDraw(engine, 2);

		assertPlayerState(bridge0, PlayerInputStatus.PLAY_CARDS);
		assertPlayerState(bridge1, PlayerInputStatus.IDLE);

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

		assertPlayerState(bridge0, PlayerInputStatus.IDLE);
		assertPlayerState(bridge1, PlayerInputStatus.PLAY_CARDS);

		run(bridge1, PlayerBridge::pass);
		assertDraw(engine, 5);

		assertPlayerState(bridge1, PlayerInputStatus.FEEDING);

		// player 0 is fed because of the long neck
		assertPlayerState(bridge0, PlayerInputStatus.IDLE);

		AutomatInstance second = engine.get().getRoot().getChilds().get(1);
		List<FeedingActionSummary> summary = second.getLocal(EvolutionGraphBuilder.FEEDING_ACTIONS);
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
	void quickPlay() {

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

		PlayArea area = PlayArea.init(Players.players(2), builder.deck());

		AuthenticatedPlayer ap0 = new AuthenticatedPlayer("p1", "Joueur 1");
		AuthenticatedPlayer ap1 = new AuthenticatedPlayer("p2", "Joueur 2");

		EvolutionGraphBuilder gbuilder = new EvolutionGraphBuilder();
		AutomatGraph graph = gbuilder.build(new EvolutionGameSettings(Arrays.asList(ap0, ap1), true));

		AutomatEngine engine = AutomatEngine.start(graph, new PlayAreaMonitor(area));

		PlayAreaMonitor monitor = monitor(engine);
		Player p0 = monitor.getArea().getPlayer(0);
		CardId c0 = p0.getHands().get(0).getId();
		Player p1 = monitor.getArea().getPlayer(1);
		CardId c1 = p1.getHands().get(0).getId();

		assertDraw(engine, 0);
		PlayerBridge bridge0 = new PlayerBridge(new PlayerRef(0, null, ap0), engine);
		PlayerBridge bridge1 = new PlayerBridge(new PlayerRef(1, null, ap1), engine);

		run(bridge0, b -> b.selectFood(new AddCardToPoolCommand(c0)));
		assertDraw(engine, 1);

		run(bridge1, b -> b.selectFood(new AddCardToPoolCommand(c1)));
		assertDraw(engine, 2);

		for (AutomatInstance ai : engine.get().getAll()) {
			logger.info("{} {}", ai.getId(), ai.getCurrent());
		}

		assertPlayerState(bridge1, PlayerInputStatus.PLAY_CARDS);
		assertPlayerState(bridge0, PlayerInputStatus.PLAY_CARDS);

		run(bridge1, PlayerBridge::pass);
		assertPlayerState(bridge1, PlayerInputStatus.IDLE);

		assertDraw(engine, 3);

		run(bridge0, b -> b.playCard(new AddTraitCommand(longNeck, new SpecieId(0, 0), 0)));
		assertDraw(engine, 4);

		run(bridge0, PlayerBridge::pass);
		assertDraw(engine, 5);

		assertPlayerState(bridge1, PlayerInputStatus.FEEDING);

		// player 0 is fed because of the long neck
		assertPlayerState(bridge0, PlayerInputStatus.IDLE);
	}

	private static void assertPlayerState(PlayerBridge pb, PlayerInputStatus expected) {
		PlayerState ps = pb.my().getLocal(EvolutionGraphBuilder.STATUS);
		assertThat(ps.getState()).isEqualTo(expected);
	}

	private static void assertDraw(AutomatEngine engine, int draw) {
		assertThat(monitor(engine).getDraw())
				.isEqualTo(draw);
	}

	private static PlayAreaMonitor monitor(AutomatEngine engine) {
		return engine.get().getRoot().getGlobal(EvolutionGraphBuilder.PLAY_AREA);
	}

	private void run(PlayerBridge bridge, Function<PlayerBridge, List<Event>> call) {
		List<Event> events = call.apply(bridge);
		logger.info("---");
		for (Event e : events) {
			logger.info("{}", e);
		}
	}
}
