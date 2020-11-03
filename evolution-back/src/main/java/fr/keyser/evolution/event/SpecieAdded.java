package fr.keyser.evolution.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.core.Card;
import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.SpeciePosition;

public class SpecieAdded extends SpecieEvent implements DiscardedEvent {

	@JsonProperty
	private final Card discarded;

	private final SpeciePosition position;

	@JsonCreator
	public SpecieAdded(@JsonProperty("src") SpecieId src, @JsonProperty("position") SpeciePosition position,
			@JsonProperty("discarded") Card discarded) {
		super(src);
		this.discarded = discarded;
		this.position = position;
	}

	@Override
	@JsonIgnore
	public int getPlayer() {
		return getSrc().getPlayer();
	}

	@JsonIgnore
	public Card getCard() {
		return discarded;
	}

	@Override
	@JsonIgnore
	public CardId getDiscarded() {
		if (discarded == null)
			return null;
		return discarded.getId();
	}

	public SpeciePosition getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return String.format("SpecieAdded [specie=%s, discarded=%s, position=%s]", getSrc(), discarded,
				position);
	}
}
