package fr.keyser.evolution.model;

public class DisableViolation {

	private final String violation;

	private final CardId discarded;

	public DisableViolation(String violation, CardId discarded) {
		this.violation = violation;
		this.discarded = discarded;
	}

	public String getViolation() {
		return violation;
	}

	public CardId getDiscarded() {
		return discarded;
	}
}
