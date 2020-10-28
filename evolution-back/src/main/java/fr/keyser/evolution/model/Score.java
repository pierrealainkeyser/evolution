package fr.keyser.evolution.model;

import static java.lang.Integer.compare;

public class Score implements Comparable<Score> {

	private final int food;

	private final int traits;

	private final int population;

	public Score(int food, int traits, int population) {
		this.food = food;
		this.traits = traits;
		this.population = population;
	}

	public int getScore() {
		return food + traits + population;
	}

	public int getFood() {
		return food;
	}

	public int getTraits() {
		return traits;
	}

	public int getPopulation() {
		return population;
	}

	@Override
	public int compareTo(Score o) {
		int cmp = compare(getScore(), o.getScore());
		if (cmp == 0)
			cmp = compare(getTraits(), o.getTraits());
		if (cmp == 0)
			cmp = compare(getPopulation(), o.getPopulation());
		return cmp;
	}
}
