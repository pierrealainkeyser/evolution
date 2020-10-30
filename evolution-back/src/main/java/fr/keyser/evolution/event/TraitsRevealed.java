package fr.keyser.evolution.event;

import java.util.Map;

import fr.keyser.evolution.core.Card;
import fr.keyser.evolution.model.SpecieId;

public class TraitsRevealed extends SpecieEvent {

	private final Map<Integer, Card> traits;

	public TraitsRevealed(SpecieId src, Map<Integer, Card> traits) {
		super(src);
		this.traits = traits;
	}

	@Override
	public String toString() {
		return String.format("TraitsRevealed [specie=%s, traits=%s]", getSrc(), traits);
	}

	public Map<Integer, Card> getTraits() {
		return traits;
	}
}
