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

	private final PlayerRef ref;

	private final AutomatEngine engine;

	public PlayerBridge(PlayerRef ref, AutomatEngine engine) {
		this.ref = ref;
		this.engine = engine;
	}

	public List<Event> pass() {
		my().unicast(new AutomatEvent(EvolutionGraphBuilder.DONE, null));
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
		my().unicast(new AutomatEvent(EvolutionGraphBuilder.INPUT, cmd));
		return events();
	}

	private List<Event> events() {
		PlayAreaMonitor monitor = my().getGlobal(EvolutionGraphBuilder.PLAY_AREA);
		return monitor.getCurrents();
	}

	public AutomatInstance my() {
		return root().getChilds().get(ref.getIndex());
	}

	public AutomatInstance root() {
		return engine.get().getRoot();
	}

}
