package fr.keyser.evolution.fsm;

import java.util.List;
import java.util.function.BiFunction;

import fr.keyser.evolution.command.AddCardToPoolCommand;
import fr.keyser.evolution.command.Command;
import fr.keyser.evolution.command.FeedingPhaseCommand;
import fr.keyser.evolution.command.PlayCardsPhaseCommand;
import fr.keyser.evolution.engine.Event;
import fr.keyser.evolution.fsm.view.CompleteRender;
import fr.keyser.evolution.fsm.view.PartialRender;
import fr.keyser.evolution.fsm.view.Renderer;
import fr.keyser.evolution.fsm.view.ViewDispatcher;
import fr.keyser.fsm.AutomatInstance;
import fr.keyser.fsm.impl.AutomatEngine;

public class BridgeService {

	private final Renderer renderer;

	private final ViewDispatcher dispatcher;

	private final GameResolver resolver;

	private final GameLocker locker;

	public BridgeService(GameLocker locker, GameResolver resolver, Renderer renderer, ViewDispatcher dispatcher) {
		this.locker = locker;
		this.resolver = resolver;
		this.renderer = renderer;
		this.dispatcher = dispatcher;
	}

	public CompleteRender connect(String uuid) {
		ResolvedRef ref = resolver.findByUuid(uuid);
		GameRef game = ref.getGame();
		PlayerRef myself = ref.getMyself();
		return locker.withinLock(game, () -> {
			AutomatEngine engine = resolver.getEngine(game);
			PlayerBridge bridge = new PlayerBridge(myself, engine);
			return renderer.complete(myself.getIndex(), game.getPlayers(), bridge.my());
		});
	}

	public void selectFood(String uuid, AddCardToPoolCommand cmd) {
		processCommand(uuid, cmd, PlayerBridge::selectFood);
	}

	public void feed(String uuid, FeedingPhaseCommand cmd) {
		processCommand(uuid, cmd, PlayerBridge::feed);
	}

	public void playCard(String uuid, PlayCardsPhaseCommand cmd) {
		processCommand(uuid, cmd, PlayerBridge::playCard);
	}

	public void pass(String uuid) {
		processCommand(uuid, null, (b, cmd) -> b.pass());
	}

	private <C extends Command> void processCommand(String uuid, C cmd,
			BiFunction<PlayerBridge, C, List<Event>> operator) {
		ResolvedRef ref = resolver.findByUuid(uuid);
		GameRef game = ref.getGame();
		locker.withinLock(game, () -> {
			AutomatEngine engine = resolver.getEngine(game);

			PlayerBridge bridge = new PlayerBridge(ref.getMyself(), engine);
			List<Event> events = operator.apply(bridge, cmd);

			// update the engine
			resolver.updateEngine(game, engine);

			// dispatch render for each player
			AutomatInstance root = bridge.root();
			for (PlayerRef p : ref.getPlayers()) {
				int index = p.getIndex();
				PartialRender partial = renderer.partial(index, root.getChilds().get(index), events);
				dispatcher.dispatch(p, partial);
			}			

		});
	}

}
