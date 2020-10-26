package fr.keyser.evolution.fsm;

import java.util.List;

import fr.keyser.evolution.command.AddCardToPoolCommand;
import fr.keyser.evolution.command.Command;
import fr.keyser.evolution.command.FeedingPhaseCommand;
import fr.keyser.evolution.command.PlayCardsPhaseCommand;
import fr.keyser.evolution.engine.Event;
import fr.keyser.fsm.AutomatEvent;
import fr.keyser.fsm.AutomatInstance;
import fr.keyser.fsm.impl.AutomatEngine;

public class PlayerBridge {

	private final int index;

	private final AutomatEngine engine;

	public PlayerBridge(int index, AutomatEngine engine) {
		this.index = index;
		this.engine = engine;
	}

	public List<Event> pass() {
		my().unicast(new AutomatEvent(EvolutionGameBuilder.DONE, null));
		return events();
	}

	public List<Event> selectFood(AddCardToPoolCommand cmd) {
		return sendCommand(cmd);
	}

	public List<Event> feed(FeedingPhaseCommand cmd) {
		return sendCommand(cmd);
	}

	public List<Event> playCard(PlayCardsPhaseCommand cmd) {
		return sendCommand(cmd);
	}

	private List<Event> sendCommand(Command cmd) {
		my().unicast(new AutomatEvent(EvolutionGameBuilder.INPUT, cmd));
		return events();
	}

	private List<Event> events() {
		PlayAreaMonitor monitor = my().getGlobal(EvolutionGameBuilder.PLAY_AREA);
		return monitor.getCurrents();
	}

	private AutomatInstance my() {
		return engine.get().getRoot().getChilds().get(index);
	}
}
