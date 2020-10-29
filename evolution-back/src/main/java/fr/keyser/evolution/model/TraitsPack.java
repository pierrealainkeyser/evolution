package fr.keyser.evolution.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TraitsPack {

	private final Trait trait;

	private final List<MetaCard> cards = new ArrayList<>();

	public TraitsPack(Trait trait, int... food) {

		this.trait = trait;
		for (int f : food) {
			cards.add(new MetaCard(trait, f));
		}
	}

	public List<MetaCard> getCards() {
		return Collections.unmodifiableList(cards);
	}

	public Trait getTrait() {
		return trait;
	}
}
