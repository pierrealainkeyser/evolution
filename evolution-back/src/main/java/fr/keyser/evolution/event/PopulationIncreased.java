package fr.keyser.evolution.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.core.Card;
import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;

public class PopulationIncreased extends PopulationChanged implements DiscardedEvent {

	@JsonProperty
	private final Card discarded;

	@JsonCreator
	public PopulationIncreased(@JsonProperty("src") SpecieId src, @JsonProperty("to") int to,
			@JsonProperty("discarded") Card discarded) {
		super(src, to);
		this.discarded = discarded;
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
		return discarded.getId();
	}

	@Override
	public String toString() {
		return String.format("PopulationIncreased [species=%s, population=%s, discarded=%s]", getSrc(), getTo(),
				discarded);
	}

}
