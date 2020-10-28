package fr.keyser.evolution.fsm;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import fr.keyser.evolution.command.AddCardToPoolCommand;
import fr.keyser.evolution.command.AddTraitCommand;
import fr.keyser.evolution.command.FeedCommand;
import fr.keyser.evolution.core.DeckBuilder;
import fr.keyser.evolution.core.PlayArea;
import fr.keyser.evolution.core.Players;
import fr.keyser.evolution.fsm.view.Renderer;
import fr.keyser.evolution.fsm.view.ViewDispatcher;
import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.Trait;
import fr.keyser.fsm.State;
import fr.keyser.fsm.impl.AutomatEngine;
import fr.keyser.fsm.impl.AutomatInstanceContainerValue;
import fr.keyser.fsm.impl.AutomatInstanceValue;
import fr.keyser.fsm.impl.graph.AutomatGraph;

public class TestBridgeService {

	private static final Logger logger = LoggerFactory.getLogger(TestBridgeService.class);

	@Test
	void nominal() {

		DeckBuilder builder = new DeckBuilder();
		CardId ambush = builder.create(Trait.AMBUSH, 4);
		CardId warningCall = builder.create(Trait.WARNING_CALL, 3);
		builder.create(Trait.BURROWING, 3);
		CardId longNeck = builder.create(Trait.LONGNECK, 7);

		CardId symbiosis = builder.create(Trait.SYMBIOSIS, 3);
		CardId climbing = builder.create(Trait.CLIMBING, 1);
		builder.create(Trait.SYMBIOSIS, 2);
		builder.create(Trait.SYMBIOSIS, 4);

		builder.create(Trait.SYMBIOSIS, 6);
		builder.create(Trait.SYMBIOSIS, 3);
		builder.create(Trait.SYMBIOSIS, 6);
		builder.create(Trait.SYMBIOSIS, 3);
		builder.create(Trait.SYMBIOSIS, 6);
		builder.create(Trait.SYMBIOSIS, 3);

		PlayArea area = PlayArea.init(Players.players(2), builder.deck());

		EvolutionGraphBuilder gbuilder = new EvolutionGraphBuilder();
		AutomatGraph graph = gbuilder.build(2);

		AutomatEngine engine = AutomatEngine.start(graph, new PlayAreaMonitor(area));

		PlayerRef p0 = new PlayerRef(0, "p1", "Joueur 1");
		PlayerRef p1 = new PlayerRef(1, "p2", "Joueur 2");

		String uuid0 = p0.getUuid();
		String uuid1 = p1.getUuid();

		GameRef gr = new GameRef("1561", Arrays.asList(p0, p1));

		ObjectMapper om = new ObjectMapper();
		ObjectWriter pp = om.writerWithDefaultPrettyPrinter();
		ViewDispatcher dispatcher = (uuid, render) -> {
			try {
				logger.info("to {} -\n{}", uuid, pp.writeValueAsString(render));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		};
		GameLocker locker = (g, r) -> r.run();

		GameResolver resolver = new GameResolver() {

			@Override
			public void setEngine(GameRef ref, AutomatEngine engine) {
			}

			@Override
			public AutomatEngine getEngine(GameRef ref) {
				return engine;
			}

			@Override
			public ResolvedRef findByUuid(String uuid) {
				if (uuid.contentEquals(uuid0))
					return new ResolvedRef(p0, gr);

				return new ResolvedRef(p1, gr);
			}
		};

		BridgeService service = new BridgeService(locker, resolver, new Renderer(), dispatcher);

		service.selectFood(uuid0, new AddCardToPoolCommand(ambush));
		service.selectFood(uuid1, new AddCardToPoolCommand(symbiosis));

		service.playCard(uuid0, new AddTraitCommand(longNeck, new SpecieId(0, 0), 0));
		service.pass(uuid0);
		service.pass(uuid1);
		service.feed(uuid1, new FeedCommand(new SpecieId(1, 1)));

		service.selectFood(uuid0, new AddCardToPoolCommand(warningCall));

		dumpState(engine);

		service.selectFood(uuid1, new AddCardToPoolCommand(climbing));

		dumpState(engine);
		service.pass(uuid1);
		service.pass(uuid0);
		service.feed(uuid1, new FeedCommand(new SpecieId(1, 1)));
		dumpState(engine);

		assertThat(engine.get().getRoot().getCurrent()).isEqualTo(new State("control", "end"));
	}

	public void dumpState(AutomatEngine engine) {
		AutomatInstanceContainerValue internal = engine.getInternal();
		for (AutomatInstanceValue aiv : internal.getAll()) {
			logger.info("{} {}", aiv.getId(), aiv.getCurrent());
		}
	}
}
