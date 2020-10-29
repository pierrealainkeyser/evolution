package fr.keyser.evolution.fsm.view;

import java.util.List;

import fr.keyser.evolution.core.TurnStep;
import fr.keyser.evolution.model.PlayerScoreBoard;

public class PlayerAreaView {

	private final List<PlayerView> players;

	private final List<PlayerScoreBoard> scoreBoards;

	private final TurnStep step;

	private final boolean lastTurn;

	private final FoodPoolView foodPool;

	public PlayerAreaView(List<PlayerView> players, List<PlayerScoreBoard> scoreBoards, TurnStep step, boolean lastTurn,
			FoodPoolView foodPool) {
		this.players = players;
		this.scoreBoards = scoreBoards;
		this.step = step;
		this.lastTurn = lastTurn;
		this.foodPool = foodPool;
	}

	public List<PlayerView> getPlayers() {
		return players;
	}

	public List<PlayerScoreBoard> getScoreBoards() {
		return scoreBoards;
	}

	public TurnStep getStep() {
		return step;
	}

	public boolean isLastTurn() {
		return lastTurn;
	}

	public FoodPoolView getFoodPool() {
		return foodPool;
	}

}
