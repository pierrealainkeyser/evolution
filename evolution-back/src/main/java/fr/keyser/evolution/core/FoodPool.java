package fr.keyser.evolution.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import fr.keyser.evolution.event.CardAddedToPool;
import fr.keyser.evolution.event.DiscardPoolFood;
import fr.keyser.evolution.event.FoodEaten;
import fr.keyser.evolution.event.PoolEvent;
import fr.keyser.evolution.event.PoolRevealed;
import fr.keyser.evolution.model.Card;
import fr.keyser.evolution.model.CardState;

public class FoodPool {

	public static final FoodPool INITIAL = new FoodPool(0, Collections.emptyList());

	private final int food;

	private final List<Card> waiting;

	private FoodPool(int food, List<Card> waiting) {
		this.food = food;
		this.waiting = Collections.unmodifiableList(waiting);
	}

	public PoolRevealed handlePoolReveal() {
		List<Card> cards = waiting.stream().map(c -> c.state(CardState::reveal)).collect(Collectors.toList());
		int delta = cards.stream().mapToInt(c -> c.getMeta().getFood()).sum();
		int total = food + delta;
		if (total < 0) {
			delta = -food;
			total = 0;
		}

		return new PoolRevealed(delta, cards, total);
	}

	public FoodPool accept(PoolEvent event) {
		if (event instanceof FoodEaten) {
			FoodEaten eaten = (FoodEaten) event;
			if (eaten.isFoodPool())
				return eat(eaten);
		} else if (event instanceof PoolRevealed) {
			return reveal((PoolRevealed) event);
		} else if (event instanceof CardAddedToPool) {
			return add((CardAddedToPool) event);
		} else if(event instanceof DiscardPoolFood ) {
			return discard((DiscardPoolFood)event);
		}

		return this;

	}

	public int getFood() {
		return food;
	}

	private FoodPool add(CardAddedToPool added) {
		List<Card> waiting = new ArrayList<>(this.waiting);
		waiting.add(added.getCard().state(CardState::pool));
		return new FoodPool(food, waiting);
	}

	private FoodPool reveal(PoolRevealed revealed) {
		return new FoodPool(revealed.getFood(), Collections.emptyList());
	}

	private FoodPool discard(DiscardPoolFood eaten) {
		int consumed = eaten.getFood();
		return new FoodPool(consumed, waiting);
	}

	private FoodPool eat(FoodEaten eaten) {
		int consumed = eaten.getConsumed();
		return new FoodPool(food - consumed, waiting);
	}
}
