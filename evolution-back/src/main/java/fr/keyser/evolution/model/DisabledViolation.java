package fr.keyser.evolution.model;

public class DisabledViolation {

	private final String violation;

	private final UsedTrait trait;

	private final UsedTrait used;

	private final Card discarded;

	public DisabledViolation(String violation, UsedTrait trait, UsedTrait used, Card discarded) {
		this.violation = violation;
		this.trait = trait;
		this.used = used;
		this.discarded = discarded;
	}

	public boolean match(Trait t) {
		if (trait == null)
			return false;
		return trait.getTrait() == t;
	}

	public UsedTrait getTrait() {
		return trait;
	}

	public Card getDiscarded() {
		return discarded;
	}

	public UsedTrait getUsed() {
		return used;
	}

	public String getViolation() {
		return violation;
	}

	@Override
	public String toString() {
		return String.format("DisabledViolation [violation=%s, trait=%s, used=%s, discarded=%s]", violation, trait,
				used, discarded);
	}
}
