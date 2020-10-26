package fr.keyser.evolution.event;

import java.util.List;

import fr.keyser.evolution.model.Card;
import fr.keyser.evolution.model.SpecieId;

public class SpecieExtincted extends SpecieEvent implements Scored, DeckEvent {

	private final int score;

	private final List<Card> traits;

	public SpecieExtincted(SpecieId src, int score, List<Card> traits) {
		super(src);
		this.score = score;
		this.traits = traits;
	}

	@Override
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
