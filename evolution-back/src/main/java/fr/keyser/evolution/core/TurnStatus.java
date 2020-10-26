package fr.keyser.evolution.core;

import java.util.Optional;

import fr.keyser.evolution.engine.Event;
import fr.keyser.evolution.event.AttackEvent;
import fr.keyser.evolution.event.LastTurnEvent;
import fr.keyser.evolution.event.NewTurnEvent;
import fr.keyser.evolution.event.NextPlayerEvent;
import fr.keyser.evolution.event.NextStepEvent;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.UsedTrait;

public class TurnStatus {

	public final static TurnStatus INITIAL = new TurnStatus(TurnStep.SELECT_FOOD, 0, 0, false, AttackRegistry.INITIAL);

	private final AttackRegistry attackRegistry;

	private final int currentPlayer;

	private final int firstPlayer;

	private final boolean lastTurn;

	private final TurnStep step;

	public TurnStatus(TurnStep step, int firstPlayer, int currentPlayer, boolean lastTurn,
			AttackRegistry attackRegistry) {
		this.step = step;
		this.firstPlayer = firstPlayer;
		this.currentPlayer = currentPlayer;
		this.lastTurn = lastTurn;
		this.attackRegistry = attackRegistry;
	}

	public TurnStatus accept(Event event) {
		if (event instanceof LastTurnEvent)
			return new TurnStatus(step, firstPlayer, currentPlayer, ((LastTurnEvent) event).isLastTurn(),
					attackRegistry);
		else if (event instanceof NewTurnEvent) {
			NewTurnEvent nte = (NewTurnEvent) event;
			return new TurnStatus(step, nte.getFirstPlayer(), nte.getFirstPlayer(), lastTurn, attackRegistry);
		} else if (event instanceof NextPlayerEvent) {
			return new TurnStatus(step, firstPlayer, ((NextPlayerEvent) event).getCurrentPlayer(), lastTurn,
					attackRegistry);
		} else if (event instanceof NextStepEvent) {
			NextStepEvent next = (NextStepEvent) event;
			return new TurnStatus(next.getStep(), firstPlayer, next.getCurrentPlayer(), lastTurn,
					AttackRegistry.INITIAL);
		} else if (event instanceof AttackEvent) {
			return new TurnStatus(step, firstPlayer, currentPlayer, lastTurn,
					attackRegistry.accept((AttackEvent) event));
		}

		return this;
	}

	public TurnStatus firstPlayer(int firstPlayer) {
		return new TurnStatus(step, firstPlayer, currentPlayer, lastTurn, attackRegistry);
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}

	public int getFirstPlayer() {
		return firstPlayer;
	}

	public TurnStep getStep() {
		return step;
	}

	public NewTurnEvent handleNewTurn(int firstPlayer) {
		return new NewTurnEvent(firstPlayer);
	}

	public boolean hasAttacked(SpecieId specieId) {
		return attackRegistry.hasAttacked(specieId);
	}

	public boolean willLoop(int nbPlayers) {
		return firstPlayer == (currentPlayer + 1) % nbPlayers;
	}

	public boolean isLastTurn() {
		return lastTurn;
	}

	public Optional<UsedTrait> quilled(SpecieId specieId) {
		return attackRegistry.quilled(specieId);
	}

	public NextPlayerEvent nextPlayer(int currentPlayer) {
		return new NextPlayerEvent(currentPlayer);
	}

	public NextStepEvent nextStep(TurnStep step) {
		return new NextStepEvent(step, firstPlayer);
	}

}
