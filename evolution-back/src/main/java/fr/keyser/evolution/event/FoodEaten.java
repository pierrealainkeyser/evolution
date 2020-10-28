package fr.keyser.evolution.event;

import java.util.List;

import fr.keyser.evolution.model.Card;
import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.FoodConsumption;
import fr.keyser.evolution.model.FoodSource;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.UsedTrait;

public class FoodEaten extends SpecieEvent implements PoolEvent, DiscardedEvent {

	private final FoodConsumption consumption;

	private final FoodSource source;

	private final List<UsedTrait> traits;

	private final Card discarded;

	public FoodEaten(SpecieId src, FoodConsumption consumption, FoodSource source, List<UsedTrait> traits,
			Card discarded) {
		super(src);
		this.source = source;
		this.consumption = consumption;
		this.traits = traits;
		this.discarded = discarded;
	}

	public int getConsumed() {
		return consumption.getConsumed();
	}

	public FoodConsumption getConsumption() {
		return consumption;
	}

	public FoodSource getSource() {
		return source;
	}

	public List<UsedTrait> getTraits() {
		return traits;
	}

	public boolean isAttack() {
		return source.isAttack();
	}

	@Override
	public int getPlayer() {
		return getSrc().getPlayer();
	}

	public Card getDiscardedCard() {
		return discarded;
	}

	@Override
	public CardId getDiscarded() {
		return discarded != null ? discarded.getId() : null;
	}

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
