package fr.keyser.evolution.fsm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import fr.keyser.evolution.command.PlayerCommand;
import fr.keyser.evolution.core.PlayArea;
import fr.keyser.evolution.core.TurnStatus;
import fr.keyser.evolution.core.TurnStep;
import fr.keyser.evolution.engine.Event;
import fr.keyser.evolution.engine.Events;
import fr.keyser.evolution.summary.FeedingActionSummaries;

public class PlayAreaMonitor {
	private final PlayArea area;

	private final List<Event> currents;

	private final List<Event> history;

	private final int draw;

	public PlayAreaMonitor(PlayArea area) {
		this(area, Collections.emptyList(), Collections.emptyList(), 0);
	}

	public PlayAreaMonitor(PlayArea area, List<Event> currents,
			List<Event> history, int draw) {
		this.area = area;
		this.currents = currents;
		this.history = history;
		this.draw = draw;
	}

	public PlayAreaMonitor addEvent(Event event) {
		List<Event> history = new ArrayList<>(this.history);
		history.add(event);

		List<Event> current = new ArrayList<Event>(this.currents);
		current.add(event);

		return new PlayAreaMonitor(area, current, history, draw);
	}

	public PlayAreaMonitor dealCards() {
		TurnStatus ts = area.getTurnStatus();
		Events<Event, PlayArea> process = area.process(ts.nextStep(TurnStep.SELECT_FOOD));
		process = process.and(process.getOutput().handleCreateSpecies());
		process = process.and(process.getOutput().handleDealCards());
		return withEvents(process);
	}

	public PlayAreaMonitor end() {
		TurnStatus ts = area.getTurnStatus();
		return withEvents(area.process(ts.nextStep(TurnStep.ENDED)));
	}

	public PlayAreaMonitor nextFirst() {
		TurnStatus ts = area.getTurnStatus();
		int nextFirst = (ts.getFirstPlayer() + 1) % area.getPlayers().size();

		return withEvents(area.process(ts.handleNewTurn(nextFirst)));
	}

	public PlayAreaMonitor acceptCommand(PlayerCommand command) {
		Event event = area.handleCommand(command);
		return withEvents(area.process(event));
	}

	public PlayAreaMonitor reset() {
		return new PlayAreaMonitor(area, Collections.emptyList(), history, draw + 1);
	}

	public PlayAreaMonitor playCards() {
		TurnStatus ts = area.getTurnStatus();
		return withEvents(area.process(ts.nextStep(TurnStep.PLAY_CARDS)));
	}

	public PlayAreaMonitor feeding() {
		TurnStatus ts = area.getTurnStatus();
		Events<Event, PlayArea> process = area.process(ts.nextStep(TurnStep.FEEDING));
		process = process.and(process.getOutput().handleTraitsRevealed());
		process = process.and(process.getOutput().handleFertiles());
		process = process.and(Arrays.asList(process.getOutput().handlePoolReveal()));
		process = process.and(process.getOutput().handleFats());
		process = process.and(process.getOutput().handleLongNecks());
		return withEvents(process);
	}

	public PlayAreaMonitor nextPlayer(boolean events) {
		TurnStatus ts = area.getTurnStatus();
		int nbPlayers = area.getPlayers().size();
		int currentPlayer = ts.getCurrentPlayer();

		return toNextPlayer((currentPlayer + 1) % nbPlayers, events);
	}

	public PlayAreaMonitor toNextPlayer(int next) {
		return toNextPlayer(next, true);
	}

	private PlayAreaMonitor toNextPlayer(int next, boolean events) {
		Events<Event, PlayArea> process = area.process(area.getTurnStatus().nextPlayer(next));
		if (events)
			return withEvents(process);
		else
			return new PlayAreaMonitor(process.getOutput(), currents, history, draw);
	}

	public Optional<ActiveFeedingPlayer> firstActive(Set<Integer> passed) {
		int nbPlayers = area.getPlayers().size();
		int currentPlayer = area.getTurnStatus().getCurrentPlayer();
		int target = currentPlayer;

		do {
			if (!passed.contains(target)) {
				FeedingActionSummaries actionsForPlayer = area.actionsForPlayer(target);
				if (!actionsForPlayer.isImpossible()) {
					return Optional.of(new ActiveFeedingPlayer(target, actionsForPlayer));
				}
			}
			target = (target + 1) % nbPlayers;
		} while (target != currentPlayer);

		return Optional.empty();
	}

	public PlayAreaMonitor cleanUp() {
		return withEvents(area.process(area.handleCleanUp()));
	}

	private PlayAreaMonitor withEvents(Events<Event, PlayArea> events) {

		List<Event> forActions = events.getEvents();

		List<Event> history = new ArrayList<>(this.history);
		history.addAll(forActions);

		List<Event> current = new ArrayList<Event>(this.currents);
		current.addAll(forActions);

		return new PlayAreaMonitor(events.getOutput(), current, history, draw);
	}

	public PlayArea getArea() {
		return area;
	}

	public List<Event> getCurrents() {
		return currents;
	}

	public List<Event> getHistory() {
		return history;
	}

	public int getDraw() {
		return draw;
	}
}
