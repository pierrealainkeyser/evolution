package fr.keyser.evolution.event;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.core.Card;
import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.FoodConsumption;
import fr.keyser.evolution.model.FoodSource;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.UsedTrait;

public class FoodEaten extends SpecieEvent implements PoolEvent, DiscardedEvent {

	@JsonProperty
	private final FoodConsumption consumption;

	private final FoodSource source;

	private final List<UsedTrait> traits;

	@JsonProperty
	private final Card discarded;

	public FoodEaten(@JsonProperty("src") SpecieId src, @JsonProperty("consumption") FoodConsumption consumption,
			@JsonProperty("source") FoodSource source, @JsonProperty("traits") List<UsedTrait> traits,
			@JsonProperty("discarded") Card discarded) {
		super(src);
		this.source = source;
		this.consumption = consumption;
		this.traits = traits;
		this.discarded = discarded;
	}

	@JsonIgnore
	public int getConsumed() {
		return consumption.getConsumed();
	}

	@JsonIgnore
	public FoodConsumption getConsumption() {
		return consumption;
	}

	public FoodSource getSource() {
		return source;
	}

	public List<UsedTrait> getTraits() {
		return traits;
	}

	@JsonIgnore
	public boolean isAttack() {
		return source.isAttack();
	}

	@JsonIgnore
	@Override
	public int getPlayer() {
		return getSrc().getPlayer();
	}

	@JsonIgnore
	public Card getDiscardedCard() {
		return discarded;
	}

	@Override
	@JsonIgnore
	public CardId getDiscarded() {
		return discarded != null ? discarded.getId() : null;
	}

	@JsonIgnore
	public boolean isFoodPool() {
		return source.isFoodPool();
	}

	@Override
	public String toString() {
		return String.format("FoodEaten [specie=%s, food=%s, fat=%s, source=%s, traits=%s, discarded=%s, delta=%s]",
				getSrc(),
				consumption.getFood(), consumption.getFat(), source, traits, discarded, -getConsumed());
	}

}
