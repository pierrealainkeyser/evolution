package fr.keyser.evolution.model;

public class AttackViolation {

	private final String type;

	private final UsedTrait trait;

	private final UsedTrait bypass;

	private final AttackViolationStatus status;

	public AttackViolation(String type, UsedTrait trait, AttackViolationStatus status, UsedTrait bypass) {
		this.type = type;
		this.trait = trait;
		this.bypass = bypass;
		this.status = status;
	}

	public UsedTrait getTrait() {
		return trait;
	}

	public String getType() {
		return type;
	}

	public UsedTrait getBypass() {
		return bypass;
	}

	public AttackViolationStatus getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return String.format("AttackViolation [type=%s, trait=%s, bypass=%s, status=%s]", type, trait, bypass, status);
	}

}
