package fr.keyser.evolution.event;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.core.Card;
import fr.keyser.evolution.model.SpecieId;

public class SpecieExtincted extends SpecieEvent implements Scored, DeckEvent {

	private final int score;

	private final List<Card> traits;

	@JsonCreator
	public SpecieExtincted(@JsonProperty("src") SpecieId src, @JsonProperty("score") int score,
			@JsonProperty("traits") List<Card> traits) {
		super(src);
		this.score = score;
		this.traits = traits;
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

	public List<Card> getTraits() {
		return traits;
	}

	@Override
	public String toString() {
		return String.format("SpecieExtincted [specie=%s, score=%s, traits=%s]", getSrc(), score, traits);
	}
}
