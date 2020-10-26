package fr.keyser.evolution.event;

import fr.keyser.evolution.model.UsedTrait;

public class DiscardPoolFood implements PoolEvent {

	private final int delta;

	private final int food;

	private final UsedTrait trait;

	public DiscardPoolFood(int delta, int food, UsedTrait trait) {
		this.delta = delta;
		this.food = food;
		this.trait = trait;
	}

	public int getDelta() {
		return delta;
	}

	public int getFood() {
		return food;
	}

	@Override
	public String toString() {
		return String.format("DiscardPoolFood [delta=%s, food=%s, trait=%s]", delta, food, trait);
	}

	public UsedTrait getTrait() {
		return trait;
	}
}
