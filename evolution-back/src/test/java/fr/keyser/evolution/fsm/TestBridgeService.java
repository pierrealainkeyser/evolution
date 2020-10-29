package fr.keyser.evolution.fsm;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import fr.keyser.evolution.command.AddCardToPoolCommand;
import fr.keyser.evolution.command.AddTraitCommand;
import fr.keyser.evolution.command.FeedCommand;
import fr.keyser.evolution.core.DeckBuilder;
import fr.keyser.evolution.core.PlayArea;
import fr.keyser.evolution.core.Players;
import fr.keyser.evolution.fsm.view.CompleteRender;
import fr.keyser.evolution.fsm.view.Renderer;
import fr.keyser.evolution.fsm.view.ViewDispatcher;
import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.EvolutionGameSettings;
import fr.keyser.evolution.model.PlayerInputStatus;
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
	void mapGameResolver() throws JsonProcessingException {

		MapGameResolver resolver = new MapGameResolver();

		ObjectMapper om = new ObjectMapper();
		ObjectWriter pp = om.writerWithDefaultPrettyPrinter();
		ViewDispatcher dispatcher = (uuid, render) -> {
			try {
				logger.info("to {} -\n{}", uuid, pp.writeValueAsString(render));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		};
		GameLocker locker = new GameLocker() {

			@Override
			public <T> T withinLock(GameRef game, Supplier<T> supplier) {
				return supplier.get();
			}
		};
		BridgeService service = new BridgeService(locker, resolver, new Renderer(), dispatcher);

		AuthenticatedPlayer ap0 = new AuthenticatedPlayer("p1", "Joueur 1");
		AuthenticatedPlayer ap1 = new AuthenticatedPlayer("p2", "Joueur 2");

		GameBuilder builder = new GameBuilder();
		ActiveGame created = builder.create(new EvolutionGameSettings(Arrays.asList(ap0, ap1), false));
		resolver.addGame(created);

		String p0 = created.getPlayers().get(0).getUuid();
		String p1 = created.getPlayers().get(1).getUuid();

		CompleteRender complete = service.connect(p0);
		assertThat(complete.getDraw())
				.isEqualTo(0);
		assertThat(complete.getGame().getPlayers())
				.hasSize(2)
				.allSatisfy(pv -> {
					assertThat(pv.getStatus()).isEqualTo(PlayerInputStatus.SELECT_FOOD);
				});

		String written = pp.writeValueAsString(complete);
		logger.info("--->{}\n{}", p0, written);
		assertThat(written)
				.doesNotContain(p0)
				.doesNotContain(p1);

		CardId first = complete.getUser().getHand().get(0).getId();

		service.selectFood(p0, new AddCardToPoolCommand(first));

		assertThat(resolver.getEngine(created.getRef()).get().getRoot().getCurrent())
				.isEqualTo(new State("control", "selectFood"));

	}

	@Test
	void nominal() throws JsonProcessingException {

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

		AuthenticatedPlayer ap0 = new AuthenticatedPlayer("p1", "Joueur 1");
		AuthenticatedPlayer ap1 = new AuthenticatedPlayer("p2", "Joueur 2");

		AutomatGraph graph = gbuilder.build(new EvolutionGameSettings(Arrays.asList(ap0, ap1), false));

		AutomatEngine engine = AutomatEngine.start(graph, new PlayAreaMonitor(area));

		PlayerRef p0 = new PlayerRef(0, "5615-15651-A", ap0);
		PlayerRef p1 = new PlayerRef(1, "51651-1321-B", ap1);

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
		GameLocker locker = new GameLocker() {

			@Override
			public <T> T withinLock(GameRef game, Supplier<T> supplier) {
				return supplier.get();
			}
		};

		GameResolver resolver = new GameResolver() {

			@Override
			public void updateEngine(GameRef ref, AutomatEngine engine) {
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

		CompleteRender complete = service.connect(uuid0);
		logger.info("--->\n{}", pp.writeValueAsString(complete));

		service.selectFood(uuid0, new AddCardToPoolCommand(ambush));
		service.selectFood(uuid1, new AddCardToPoolCommand(symbiosis));

		service.playCard(uuid0, new AddTraitCommand(longNeck, new SpecieId(0, 0), 0));

		complete = service.connect(uuid0);
		logger.info("--->\n{}", pp.writeValueAsString(complete));

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

		complete = service.connect(uuid0);
		logger.info("--->\n{}", pp.writeValueAsString(complete));

		assertThat(engine.get().getRoot().getCurrent())
				.isEqualTo(new State("control", "end"));
	}

	public void dumpState(AutomatEngine engine) {
		AutomatInstanceContainerValue internal = engine.getInternal();
		for (AutomatInstanceValue aiv : internal.getAll()) {
			logger.info("{} {}", aiv.getId(), aiv.getCurrent());
		}
	}
}
