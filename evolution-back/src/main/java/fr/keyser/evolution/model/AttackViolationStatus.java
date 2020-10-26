package fr.keyser.evolution.model;

public enum AttackViolationStatus {
	BLOCK, BYPASS, ACCEPT, MUST_PAY, MAY_PAY;

	public boolean isPayable() {
		return MAY_PAY == this || MUST_PAY == this;
	}

	public boolean isPayRequired() {
		return MUST_PAY == this;
	}

	public boolean isBlocked() {
		return BLOCK == this;
	}

	public boolean isAccepted() {
		return ACCEPT == this;
	}
}
