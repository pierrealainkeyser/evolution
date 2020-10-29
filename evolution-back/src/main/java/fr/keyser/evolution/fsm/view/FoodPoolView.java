package fr.keyser.evolution.fsm.view;

import fr.keyser.evolution.core.FoodPool;

public class FoodPoolView {

	private final FoodPool foodPool;

	public FoodPoolView(FoodPool foodPool) {
		this.foodPool = foodPool;
	}

	public int getFood() {
		return foodPool.getFood();
	}

	public int getWaiting() {
		return foodPool.getWaiting().size();
	}

}
