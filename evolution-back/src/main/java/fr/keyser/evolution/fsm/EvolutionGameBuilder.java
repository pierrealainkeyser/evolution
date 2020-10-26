package fr.keyser.evolution.fsm;

import static fr.keyser.fsm.impl.transition.TransitionSourceBuilder.check;
import static fr.keyser.fsm.impl.transition.TransitionSourceBuilder.join;
import static fr.keyser.fsm.impl.transition.TransitionSourceBuilder.route;
import static fr.keyser.fsm.impl.transition.TransitionSourceBuilder.to;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import fr.keyser.evolution.command.AddCardToPoolCommand;
import fr.keyser.evolution.command.Command;
import fr.keyser.evolution.command.FeedingPhaseCommand;
import fr.keyser.evolution.command.PlayCardsPhaseCommand;
import fr.keyser.evolution.command.PlayerCommand;
import fr.keyser.evolution.core.PlayArea;
import fr.keyser.evolution.core.TurnStatus;
import fr.keyser.fsm.AutomatEvent;
import fr.keyser.fsm.AutomatInstance;
import fr.keyser.fsm.AutomatLifeCycleEvent;
import fr.keyser.fsm.FollowedTransition;
import fr.keyser.fsm.impl.graph.AutomatGraph;
import fr.keyser.fsm.impl.graph.GraphBuilder;
import fr.keyser.fsm.impl.graph.GraphBuilder.NodeBuilder;

public class EvolutionGameBuilder {

	public final static String PLAY_AREA = "playarea";

	public final static String SUMMARY = "summary";

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

		public PlayerFlow(NodeBuilder root) {

			NodeBuilder idle = root.sub("idle");

			NodeBuilder selectFood = root.sub("selectFood");
			NodeBuilder playCards = root.sub("playCards");
			NodeBuilder feeding = root.sub("feeding");

			idle.transition(route()
					.on(PlayerFlowEnum.SELECT_FOOD, to(selectFood))
					.on(PlayerFlowEnum.PLAY_CARDS, to(playCards))
					.on(PlayerFlowEnum.FEEDING, check(this::checkSummary, to(feeding))));

			selectFood(selectFood, idle);
			playCards(playCards, idle);
			feeding(feeding, idle);
		}

		boolean checkSummary(AutomatInstance instance, AutomatEvent event) {
			Object payload = event.getPayload();
			return payload instanceof List;
		}

		private void selectFood(NodeBuilder selectFood, NodeBuilder idle) {
			NodeBuilder wait = selectFood.sub("wait");
			selectFoodDone = selectFood.sub("done");

			wait.transition(route().on(INPUT, check(this::checkAddCardToPool, to(selectFoodDone))));
			wait.leave(this::command);

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
			NodeBuilder wait = playCards.sub("wait");
			NodeBuilder play = playCards.sub("play");
			NodeBuilder pass = playCards.sub("pass");

			wait.transition(route()
					.on(INPUT, check(this::checkPlayCardsPhaseCommand, to(play)))
					.on(DONE, to(pass)));

			play.entry(this::command);
			play.auto(wait);

			pass.entry(this::reset);
			pass.auto(idle);

			playCards.leave(this::done);

		}

		private boolean checkPlayCardsPhaseCommand(AutomatInstance instance, AutomatEvent event) {
			Object payload = event.getPayload();
			return payload instanceof PlayCardsPhaseCommand;
		}

		private void feeding(NodeBuilder feeding, NodeBuilder idle) {
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
			return instance.setLocal(SUMMARY, transition.getPayload());
		}

		private AutomatInstance reset(AutomatInstance instance) {
			return instance.updateGlobal(PLAY_AREA, PlayAreaMonitor::reset);
		}

		private AutomatInstance done(AutomatInstance instance) {
			instance.getParent().ifPresent(p -> p.unicast(ControlFlowEnum.DONE));
			return instance;
		}

		private AutomatInstance fed(AutomatInstance instance) {
			return instance.setLocal(SUMMARY, null);
		}
	}

	private class ControlFlow {
		private final NodeBuilder selectFood;

		public ControlFlow(NodeBuilder root) {
			selectFood = root.sub("selectFood");
			NodeBuilder playCards = root.sub("playCards");
			NodeBuilder feeding = root.sub("feeding");
			NodeBuilder cleanUp = root.sub("cleanUp");
			NodeBuilder checkEog = root.sub("checkEog");
			NodeBuilder end = root.sub("end");

			selectFood(selectFood, playCards);
			playCards(playCards, feeding);
			feeding(feeding, cleanUp);
			cleanUp(cleanUp, selectFood, checkEog);

		}

		private void selectFood(NodeBuilder select, NodeBuilder playCards) {
			select.entry(this::selectFood);
			select.transition(join(to(playCards)));
		}

		private AutomatInstance selectFood(AutomatInstance instance) {
			return instance.updateGlobal(PLAY_AREA, PlayAreaMonitor::dealCards)
					.multicast(instance.getChildsId(), PlayerFlowEnum.SELECT_FOOD);

		}

		private void playCards(NodeBuilder playCards, NodeBuilder feeding) {
			playCards.entry(this::playCards);

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
			feeding.entry(this::feeding);

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

			clean.entry(this::cleanUp);
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

		private AutomatInstance cleanUp(AutomatInstance instance) {
			return instance.updateGlobal(PLAY_AREA, PlayAreaMonitor::cleanUp);
		}

		private AutomatInstance playCards(AutomatInstance instance) {
			return instance.updateGlobal(PLAY_AREA, PlayAreaMonitor::playCards);
		}

		private AutomatInstance nextFirst(AutomatInstance instance) {
			return instance.updateGlobal(PLAY_AREA, PlayAreaMonitor::nextFirst);
		}

		private AutomatInstance feeding(AutomatInstance instance) {
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

	public AutomatGraph build(int nbPlayers) {

		GraphBuilder builder = new GraphBuilder();

		NodeBuilder initial = builder.initial();
		NodeBuilder fork = builder.root("fork");
		NodeBuilder control = builder.root("control");
		NodeBuilder player = builder.root("player");

		initial.transition(route().on(AutomatLifeCycleEvent.START, to(fork)));
		initial.leave(this::bootstrap);

		fork.fork(control, nbPlayers, player);

		ControlFlow controlFlow = new ControlFlow(control);
		PlayerFlow playerFlow = new PlayerFlow(player);

		builder.rendezVous(controlFlow.selectFood, playerFlow.selectFoodDone);

		return builder.build();
	}

	private AutomatInstance bootstrap(AutomatInstance a, FollowedTransition t) {
		PlayAreaMonitor bootstrap = (PlayAreaMonitor) t.getPayload();
		return a.setGlobal(PLAY_AREA, bootstrap);
	}
}
