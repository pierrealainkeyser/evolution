package fr.keyser.evolution.event;

import fr.keyser.evolution.model.SpecieId;

public class FoodScored extends SpecieEvent implements Scored {

	private final int score;

	public FoodScored(SpecieId src, int score) {
		super(src);
		this.score = score;
	}

	@Override
	public int getPlayer() {
		return getSrc().getPlayer();
	}

	@Override
	public int getScore() {
		return score;
	}

	@Override
	public String toString() {
		return String.format("FoodScored [specie=%s, score=%s]", getSrc(), score);
	}
}
