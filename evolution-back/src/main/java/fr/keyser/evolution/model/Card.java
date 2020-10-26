package fr.keyser.evolution.model;

import java.util.function.UnaryOperator;

public class Card {

	private final CardId id;

	private final MetaCard meta;

	private final CardState state;

	public Card(CardId id, MetaCard meta, CardState state) {
		this.id = id;
		this.meta = meta;
		this.state = state;
	}

	public Card state(UnaryOperator<CardState> op) {
		return new Card(id, meta, op.apply(state));
	}

	public CardId getId() {
		return id;
	}

	public MetaCard getMeta() {
		return meta;
	}

	public CardState getState() {
		return state;
	}

	@Override
	public String toString() {
		return String.format("Card [id=%s, trait=%s, food=%d]", id, meta.getTrait(), meta.getFood());
	}
}
