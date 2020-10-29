package fr.keyser.evolution.model;

public class MetaCard {

	private final MetaCardId id;

	private final Trait trait;

	private final int food;

	public MetaCard(Trait trait, int food) {
		this(new MetaCardId(trait.name().toLowerCase() + "-" + food), trait, food);
	}

	public MetaCard(MetaCardId id, Trait trait, int food) {
		this.id = id;
		this.trait = trait;
		this.food = food;
	}

	public Trait getTrait() {
		return trait;
	}

	public int getFood() {
		return food;
	}

	public MetaCardId getId() {
		return id;
	}
}
