package fr.keyser.evolution.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FoodConsumption {

	private final int food;

	private final int fat;

	@JsonCreator
	public FoodConsumption(@JsonProperty("food") int food, @JsonProperty("fat") int fat) {
		this.food = food;
		this.fat = fat;
	}

	@JsonIgnore
	public boolean isEmpty() {
		return getConsumed() == 0;
	}

	@JsonIgnore
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
