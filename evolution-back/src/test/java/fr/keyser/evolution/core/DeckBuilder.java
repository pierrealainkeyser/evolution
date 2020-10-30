package fr.keyser.evolution.core;

import java.util.LinkedHashMap;
import java.util.Map;

import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.CardState;
import fr.keyser.evolution.model.MetaCard;
import fr.keyser.evolution.model.Trait;

public class DeckBuilder {
	private int count = 0;

	private Map<CardId, MetaCard> metas = new LinkedHashMap<>();

	public CardId create(Trait trait, int food) {
		CardId id = new CardId(count++);
		MetaCard meta = new MetaCard(trait, food);
		metas.put(id, meta);
		return id;
	}

	public CardId create(Trait trait) {
		return create(trait, 0);
	}

	public Card card(Trait trait, int food) {
		CardId id = new CardId(count++);
		MetaCard meta = new MetaCard(trait, food);
		metas.put(id, meta);
		return new Card(id, meta, CardState.INITIAL);
	}

	public Card card(Trait trait) {
		return card(trait, 0);
	}

	public Deck deck() {
		return Deck.create(metas);
	}
}