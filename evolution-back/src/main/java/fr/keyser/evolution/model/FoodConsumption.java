package fr.keyser.evolution.model;

public class FoodConsumption {

	private final int food;

	private final int fat;

	public FoodConsumption(int food, int fat) {
		this.food = food;
		this.fat = fat;
	}

	public boolean isEmpty() {
		return getConsumed() == 0;
	}

	public int getConsumed() {
		return food + fat;
	}

	public int getFood() {
		return food;
	}

	public int getFat() {
		return fat;
	}
}
