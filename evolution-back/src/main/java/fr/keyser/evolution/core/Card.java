package fr.keyser.evolution.core;

import java.util.function.UnaryOperator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.CardState;
import fr.keyser.evolution.model.MetaCard;

public class Card {

	private final static ThreadLocal<Deck> currentDeck = new ThreadLocal<>();

	public static void useDeck(Deck deck) {
		currentDeck.set(deck);
	}

	public static void removeDeck() {
		currentDeck.remove();
	}

	private final CardId id;

	private final MetaCard meta;

	private final CardState state;

	@JsonCreator
	public Card(@JsonProperty("id") CardId id, @JsonProperty("state") CardState state) {
		this(id, currentDeck.get().getMeta(id), state);
	}

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

	@JsonIgnore
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
