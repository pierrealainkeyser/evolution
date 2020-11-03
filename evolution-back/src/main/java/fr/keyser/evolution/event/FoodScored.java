package fr.keyser.evolution.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.model.SpecieId;

public class FoodScored extends SpecieEvent implements Scored {

	private final int score;

	@JsonCreator
	public FoodScored(@JsonProperty("src") SpecieId src, @JsonProperty("score") int score) {
		super(src);
		this.score = score;
	}

	@Override
	@JsonIgnore
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
