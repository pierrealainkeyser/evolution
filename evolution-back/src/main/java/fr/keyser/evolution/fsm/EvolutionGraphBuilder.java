package fr.keyser.evolution.fsm;

import static fr.keyser.fsm.impl.transition.TransitionSourceBuilder.check;
import static fr.keyser.fsm.impl.transition.TransitionSourceBuilder.join;
import static fr.keyser.fsm.impl.transition.TransitionSourceBuilder.route;
import static fr.keyser.fsm.impl.transition.TransitionSourceBuilder.to;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import fr.keyser.evolution.command.AddCardToPoolCommand;
import fr.keyser.evolution.command.Command;
import fr.keyser.evolution.command.FeedingPhaseCommand;
import fr.keyser.evolution.command.PlayCardsPhaseCommand;
import fr.keyser.evolution.command.PlayerCommand;
import fr.keyser.evolution.core.PlayArea;
import fr.keyser.evolution.core.PlayerState;
import fr.keyser.evolution.core.TurnStatus;
import fr.keyser.evolution.event.PlayerPassedEvent;
import fr.keyser.evolution.event.PlayerStateChanged;
import fr.keyser.evolution.model.EvolutionGameParameters;
import fr.keyser.evolution.model.PlayerInputStatus;
import fr.keyser.evolution.summary.FeedingActionSummaries;
import fr.keyser.fsm.AutomatEvent;
import fr.keyser.fsm.AutomatInstance;
import fr.keyser.fsm.AutomatLifeCycleEvent;
import fr.keyser.fsm.FollowedTransition;
import fr.keyser.fsm.State;
import fr.keyser.fsm.impl.AutomatEngine;
import fr.keyser.fsm.impl.graph.AutomatGraph;
import fr.keyser.fsm.impl.graph.GraphBuilder;
import fr.keyser.fsm.impl.graph.GraphBuilder.NodeBuilder;

public class EvolutionGraphBuilder {

	private final static State ENDED = new State("control", "end");

	public final static String PLAY_AREA = "playarea";

	public final static String FEEDING_ACTIONS = "feedingactions";

	public final static String SCOREBOARDS = "scoreboards";

	public final static String STATUS = "status";

	public final static String INPUT = "input";

	public final static String DONE = "done";

	private static enum PlayerFlowEnum implements Supplier<AutomatEvent> {
		SELECT_FOOD, PLAY_CARDS, FEEDING;

		private final AutomatEvent event;

		PlayerFlowEnum() {
			this.event = event(null);
		}

		public AutomatEvent event(Object payload) {
			return new AutomatEvent(this, payload);
		}

		@Override
		public AutomatEvent get() {
			return event;
		}
	}

	private static enum ControlFlowEnum implements Supplier<AutomatEvent> {
		WAIT, DONE, NOTHING;

		private final AutomatEvent event;

		ControlFlowEnum() {
			this.event = event(null);
		}

		public AutomatEvent event(Object payload) {
			return new AutomatEvent(this, payload);
		}

		@Override
		public AutomatEvent get() {
			return event;
		}

	}

	private class PlayerFlow {

		private NodeBuilder selectFoodDone;

		private NodeBuilder quickPlayCardsDone;

		public PlayerFlow(NodeBuilder root, EvolutionGameParameters settings) {

			NodeBuilder start = root.sub("start");
			NodeBuilder idle = root.sub("idle");

			NodeBuilder selectFood = root.sub("selectFood");
			NodeBuilder playCards = root.sub("playCards");
			NodeBuilder feeding = root.sub("feeding");

			start.entry(this::start);
			start.auto(idle);

			idle.entry(playerState(PlayerInputStatus.IDLE));
			idle.transition(route()
					.on(PlayerFlowEnum.SELECT_FOOD, to(selectFood))
					.on(PlayerFlowEnum.PLAY_CARDS, to(playCards))
					.on(PlayerFlowEnum.FEEDING, check(this::checkSummary, to(feeding))));

			selectFood(selectFood, idle);
			if (settings.isQuickplay())
				quickPlayCards(playCards, idle);
			else
				playCards(playCards, idle);
			feeding(feeding, idle);
		}

		boolean checkSummary(AutomatInstance instance, AutomatEvent event) {
			Object payload = event.getPayload();
			return payload instanceof FeedingActionSummaries;
		}

		private void selectFood(NodeBuilder selectFood, NodeBuilder idle) {
			selectFood.entry(playerState(PlayerInputStatus.SELECT_FOOD));

			NodeBuilder wait = selectFood.sub("wait");
			selectFoodDone = selectFood.sub("done");

			wait.transition(route().on(INPUT, check(this::checkAddCardToPool, to(selectFoodDone))));
			wait.leave(this::command);
			wait.leave(playerState(PlayerInputStatus.IDLE));

			selectFoodDone.transition(join(to(idle)));
		}

		private boolean checkAddCardToPool(AutomatInstance instance, AutomatEvent event) {
			Object payload = event.getPayload();
			return payload instanceof AddCardToPoolCommand;
		}

		private AutomatInstance command(AutomatInstance instance, FollowedTransition transition) {
			Command payload = (Command) transition.getPayload();
			return instance.updateGlobal(PLAY_AREA,
					(PlayAreaMonitor monitor) -> monitor.reset()
							.acceptCommand(new PlayerCommand(instance.getIndex(), payload)));
		}

		private void playCards(NodeBuilder playCards, NodeBuilder idle) {
			playCards.entry(playerState(PlayerInputStatus.PLAY_CARDS));

			NodeBuilder wait = playCards.sub("wait");
			NodeBuilder play = playCards.sub("play");
			NodeBuilder pass = playCards.sub("pass");

			playCardsLoop(wait, play, pass);

			pass.entry(this::pass);
			pass.auto(idle);

			playCards.leave(this::done);
		}

		private void playCardsLoop(NodeBuilder wait, NodeBuilder play, NodeBuilder done) {
			wait.transition(route()
					.on(INPUT, check(this::checkPlayCardsPhaseCommand, to(play)))
					.on(DONE, to(done)));

			play.entry(this::command);
			play.auto(wait);
		}

		private void quickPlayCards(NodeBuilder playCards, NodeBuilder idle) {
			playCards.entry(playerState(PlayerInputStatus.PLAY_CARDS));

			NodeBuilder wait = playCards.sub("wait");
			NodeBuilder play = playCards.sub("play");
			quickPlayCardsDone = playCards.sub("done");

			playCardsLoop(wait, play, quickPlayCardsDone);

			quickPlayCardsDone.entry(this::pass);
			quickPlayCardsDone.entry(playerState(PlayerInputStatus.IDLE));
			quickPlayCardsDone.transition(join(to(idle)));
		}

		private AutomatInstance pass(AutomatInstance instance) {
			PlayerPassedEvent event = new PlayerPassedEvent(instance.getIndex());
			return instance.updateGlobal(PLAY_AREA, (PlayAreaMonitor pm) -> pm.reset().addEvent(event));
		}

		private AutomatInstance start(AutomatInstance instance) {
			return instance.setLocal(STATUS, PlayerState.INITIAL);
		}

		private UnaryOperator<AutomatInstance> playerState(PlayerInputStatus status) {
			return instance -> {

				PlayerState state = instance.getLocal(STATUS);
				if (state.getState() == status)
					return instance;

				PlayerStateChanged changed = new PlayerStateChanged(instance.getIndex(), status);
				return instance.updateGlobal(PLAY_AREA, (PlayAreaMonitor pm) -> pm.addEvent(changed))
						.updateLocal(STATUS, (PlayerState ps) -> ps.accept(changed));
			};
		}

		private boolean checkPlayCardsPhaseCommand(AutomatInstance instance, AutomatEvent event) {
			Object payload = event.getPayload();
			return payload instanceof PlayCardsPhaseCommand;
		}

		private void feeding(NodeBuilder feeding, NodeBuilder idle) {
			feeding.entry(playerState(PlayerInputStatus.FEEDING));

			NodeBuilder wait = feeding.sub("wait");
			NodeBuilder play = feeding.sub("play");

			wait.entry(this::registerSummary);
			wait.transition(route()
					.on(INPUT, check(this::checkFeedingPhaseCommand, to(play)))
					.on(DONE, to(idle)));

			play.entry(this::command);
			play.auto(idle);

			feeding.leave(this::fed);
			feeding.leave(this::done);

		}

		private boolean checkFeedingPhaseCommand(AutomatInstance instance, AutomatEvent event) {
			Object payload = event.getPayload();
			return payload instanceof FeedingPhaseCommand;
		}

		private AutomatInstance registerSummary(AutomatInstance instance, FollowedTransition transition) {
			return instance.setLocal(FEEDING_ACTIONS, transition.getPayload());
		}

		private AutomatInstance done(AutomatInstance instance) {
			instance.getParent().ifPresent(p -> p.unicast(ControlFlowEnum.DONE));
			return instance;
		}

		private AutomatInstance fed(AutomatInstance instance) {
			return instance.setLocal(FEEDING_ACTIONS, null);
		}
	}

	private class ControlFlow {
		private final NodeBuilder selectFood;

		private final NodeBuilder playCards;

		public ControlFlow(NodeBuilder root, EvolutionGameParameters settings) {
			selectFood = root.sub("selectFood");
			playCards = root.sub("playCards");
			NodeBuilder feeding = root.sub("feeding");
			NodeBuilder cleanUp = root.sub("cleanUp");
			NodeBuilder end = root.sub("end");

			selectFood(selectFood, playCards);
			if (settings.isQuickplay())
				quickPlayCards(playCards, feeding);
			else
				playCards(playCards, feeding);
			feeding(feeding, cleanUp);
			cleanUp(cleanUp, selectFood, end);
			end(end);

		}

		private void end(NodeBuilder end) {
			end.entry(this::end);
		}

		private AutomatInstance end(AutomatInstance instance) {
			PlayAreaMonitor monitor = instance.getGlobal(PLAY_AREA);
			return instance.setGlobal(SCOREBOARDS, monitor.getArea().scoreBoards())
					.setGlobal(PLAY_AREA, monitor.end());
		}

		private void selectFood(NodeBuilder select, NodeBuilder playCards) {
			select.entry(this::doSelectFood);
			select.transition(join(to(playCards)));
		}

		private AutomatInstance doSelectFood(AutomatInstance instance) {
			return instance.updateGlobal(PLAY_AREA, PlayAreaMonitor::dealCards)
					.multicast(instance.getChildsId(), PlayerFlowEnum.SELECT_FOOD);
		}

		private void quickPlayCards(NodeBuilder playCards, NodeBuilder feeding) {
			playCards.entry(this::doQuickPlayCards);
			playCards.transition(join(to(feeding)));
		}

		private AutomatInstance doQuickPlayCards(AutomatInstance instance) {
			return instance.updateGlobal(PLAY_AREA, PlayAreaMonitor::playCards)
					.multicast(instance.getChildsId(), PlayerFlowEnum.PLAY_CARDS);
		}

		private void playCards(NodeBuilder playCards, NodeBuilder feeding) {
			playCards.entry(this::doPlayCards);

			NodeBuilder loop = playCards.sub("loop");
			NodeBuilder check = playCards.sub("check");
			NodeBuilder next = playCards.sub("next");

			loop.entry(this::forwardPlayCards);
			loop.transition(route().on(ControlFlowEnum.DONE, to(check)));

			check.choice(c -> c.when(this::willLoop, to(feeding))
					.orElse(to(next)));

			next.entry(this::nextPlayerEvent);
			next.auto(loop);
		}

		private void feeding(NodeBuilder feeding, NodeBuilder cleanUp) {
			feeding.entry(this::doFeeding);

			NodeBuilder loop = feeding.sub("loop");
			NodeBuilder wait = feeding.sub("wait");

			loop.entry(this::nextFeedingActions);
			loop.transition(route()
					.on(ControlFlowEnum.WAIT, to(wait))
					.on(ControlFlowEnum.NOTHING, to(cleanUp)));

			wait.transition(route()
					.on(ControlFlowEnum.DONE, to(loop)));
			wait.leave(this::nextPlayer);
		}

		private void cleanUp(NodeBuilder cleanUp, NodeBuilder selectFood, NodeBuilder end) {
			NodeBuilder clean = cleanUp.sub("clean");
			NodeBuilder restart = cleanUp.sub("restart");

			clean.entry(this::doCleanUp);
			clean.choice(c -> c.when(this::isLastTurn, to(end))
					.orElse(to(restart)));

			restart.entry(this::nextFirst);
			restart.auto(selectFood);
		}

		private boolean isLastTurn(AutomatInstance instance, AutomatEvent event) {
			PlayAreaMonitor monitor = instance.getGlobal(PLAY_AREA);
			TurnStatus ts = monitor.getArea().getTurnStatus();
			return ts.isLastTurn();
		}

		private boolean willLoop(AutomatInstance instance, AutomatEvent event) {
			PlayAreaMonitor monitor = instance.getGlobal(PLAY_AREA);
			PlayArea area = monitor.getArea();
			TurnStatus ts = area.getTurnStatus();
			return ts.willLoop(area.getPlayers().size());
		}

		private AutomatInstance doCleanUp(AutomatInstance instance) {
			return instance.updateGlobal(PLAY_AREA, PlayAreaMonitor::cleanUp);
		}

		private AutomatInstance doPlayCards(AutomatInstance instance) {
			return instance.updateGlobal(PLAY_AREA, PlayAreaMonitor::playCards);
		}

		private AutomatInstance nextFirst(AutomatInstance instance) {
			return instance.updateGlobal(PLAY_AREA, PlayAreaMonitor::nextFirst);
		}

		private AutomatInstance doFeeding(AutomatInstance instance) {
			return instance.updateGlobal(PLAY_AREA, PlayAreaMonitor::feeding);
		}

		private AutomatInstance nextPlayer(AutomatInstance instance) {
			return instance.updateGlobal(PLAY_AREA, (PlayAreaMonitor p) -> p.nextPlayer(false));
		}

		private AutomatInstance nextPlayerEvent(AutomatInstance instance) {
			return instance.updateGlobal(PLAY_AREA, (PlayAreaMonitor p) -> p.nextPlayer(true));
		}

		private AutomatInstance forwardPlayCards(AutomatInstance instance) {
			PlayAreaMonitor monitor = instance.getGlobal(PLAY_AREA);
			TurnStatus ts = monitor.getArea().getTurnStatus();
			int currentPlayer = ts.getCurrentPlayer();
			instance.getChilds().get(currentPlayer).unicast(PlayerFlowEnum.PLAY_CARDS);
			return instance;
		}

		private AutomatInstance nextFeedingActions(AutomatInstance instance) {
			PlayAreaMonitor monitor = instance.getGlobal(PLAY_AREA);

			Optional<ActiveFeedingPlayer> firstActive = monitor.firstActive();
			if (firstActive.isPresent()) {

				ActiveFeedingPlayer afp = firstActive.get();
				// send the feeding instruction to the child
				int player = afp.getPlayer();

				// wait for the input of the player
				instance.unicast(ControlFlowEnum.WAIT);

				// change the active player
				instance = instance.setGlobal(PLAY_AREA, monitor.toNextPlayer(player));

				instance.getChilds().get(player)
						.unicast(PlayerFlowEnum.FEEDING.event(afp.getSummary()));
			} else {
				instance.unicast(ControlFlowEnum.NOTHING);
			}

			return instance;
		}

	}

	public boolean isTerminated(AutomatEngine engine) {

		return ENDED.equals(engine.get().getRoot().getCurrent());
	}

	public AutomatGraph build(EvolutionGameParameters settings) {

		GraphBuilder builder = new GraphBuilder();

		NodeBuilder initial = builder.initial();
		NodeBuilder fork = builder.root("fork");
		NodeBuilder control = builder.root("control");
		NodeBuilder player = builder.root("player");

		initial.transition(route().on(AutomatLifeCycleEvent.START, to(fork)));
		initial.leave(this::bootstrap);

		fork.fork(control, settings.getPlayersCount(), player);

		ControlFlow controlFlow = new ControlFlow(control, settings);
		PlayerFlow playerFlow = new PlayerFlow(player, settings);

		builder.rendezVous(controlFlow.selectFood, playerFlow.selectFoodDone);
		if (settings.isQuickplay())
			builder.rendezVous(controlFlow.playCards, playerFlow.quickPlayCardsDone);

		return builder.build();
	}

	private AutomatInstance bootstrap(AutomatInstance a, FollowedTransition t) {
		PlayAreaMonitor bootstrap = (PlayAreaMonitor) t.getPayload();
		return a.setGlobal(PLAY_AREA, bootstrap);
	}
}
