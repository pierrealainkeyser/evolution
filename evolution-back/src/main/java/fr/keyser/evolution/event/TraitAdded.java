package fr.keyser.evolution.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.core.Card;
import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;

public class TraitAdded extends SpecieEvent implements DiscardedEvent {

	private final Card card;
	private final int index;
	private final Card replaced;

	@JsonCreator
	public TraitAdded(@JsonProperty("src") SpecieId src, @JsonProperty("card") Card card,
			@JsonProperty("index") int index, @JsonProperty("replaced") Card replaced) {
		super(src);
		this.card = card;
		this.index = index;
		this.replaced = replaced;
	}

	public Card getCard() {
		return card;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public String toString() {
		return String.format("TraitAdded [specie=%s, card=%s, index=%s, replaced=%s]", getSrc(), card, index, replaced);
	}

	@Override
	@JsonIgnore
	public CardId getDiscarded() {
		if (replaced != null)
			return replaced.getId();

		return null;
	}

	@Override
	@JsonIgnore
	public int getPlayer() {
		return getSrc().getPlayer();
	}

	public Card getReplaced() {
		return replaced;
	}

}
