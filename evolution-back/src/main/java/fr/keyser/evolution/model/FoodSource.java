package fr.keyser.evolution.model;

public enum FoodSource {
	POOL, PLANT, ATTACK, MEAT;

	public boolean isPlantBased() {
		return POOL == this || PLANT == this;
	}

	public boolean isFoodPool() {
		return POOL == this;
	}

	public boolean isAttack() {
		return ATTACK == this;
	}

	public FoodSource derive() {
		if (isAttack())
			return MEAT;
		else
			return this;
	}

}
