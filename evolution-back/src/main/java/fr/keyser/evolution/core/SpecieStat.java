package fr.keyser.evolution.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.model.FoodConsumption;
import fr.keyser.evolution.model.FoodSource;

public class SpecieStat {

	public static final SpecieStat INITIAL = new SpecieStat(1, 1, 0, 0);

	private final int fat;

	private final int food;

	private final int population;

	private final int size;

	@JsonCreator
	public SpecieStat(@JsonProperty("size") int size, @JsonProperty("population") int population,
			@JsonProperty("food") int food, @JsonProperty("fat") int fat) {
		this.size = size;
		this.population = population;
		this.food = food;
		this.fat = fat;
	}

	@Override
	public String toString() {
		return String.format("population=%s, size=%s, food=%s, fat=%s", population, size, food, fat);
	}

	public int accumulatedFood() {
		return food + fat;
	}

	public int consumable(Traits traits, FoodSource source, int batchSize) {
		int hungryNess = hungryNess(traits);

		if (source.isPlantBased() && traits.isForaging())
			++batchSize;

		return Math.min(batchSize, hungryNess);
	}

	public FoodConsumption consumption(Traits traits, int eaten) {

		int nominal = Math.max(0, population - food);
		int direct = Math.min(eaten, nominal);
		int fat = 0;

		if (traits.isFatTissue()) {
			int remaining = eaten - direct;
			int fatSpace = Math.max(0, size - fat);
			fat = Math.min(remaining, fatSpace);
		}
		return new FoodConsumption(direct, fat);
	}

	public SpecieStat score() {
		return new SpecieStat(size, population, 0, fat);
	}

	public SpecieStat eat(FoodConsumption consumption) {
		return new SpecieStat(size, population, food + consumption.getFood(), fat + consumption.getFat());
	}

	public int getFood() {
		return food;
	}

	public int getFat() {
		return fat;
	}

	public int getPopulation() {
		return population;
	}

	public int getSize() {
		return size;
	}

	@JsonIgnore
	public boolean isFed() {
		return food >= population;
	}

	public int hungryNess(Traits traits) {
		int max = Math.max(0, population - food);
		if (traits.isFatTissue()) {
			max += Math.max(0, size - fat);
		}

		return max;
	}

	public SpecieStat moveFat(int amount) {
		return new SpecieStat(size, population, food + amount, fat - amount);
	}

	public SpecieStat population(int population) {
		return new SpecieStat(size, population, food, fat);
	}

	public SpecieStat size(int size) {
		return new SpecieStat(size, population, food, fat);
	}

}
