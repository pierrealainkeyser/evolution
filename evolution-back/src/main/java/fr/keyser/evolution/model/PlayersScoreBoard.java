package fr.keyser.evolution.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class PlayersScoreBoard {

	private final List<PlayerScoreBoard> boards;

	@JsonCreator
	public PlayersScoreBoard(@JsonUnwrapped List<PlayerScoreBoard> boards) {
		this.boards = boards;
	}

	@JsonUnwrapped
	public List<PlayerScoreBoard> getBoards() {
		return boards;
	}

}
