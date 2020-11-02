package fr.keyser.evolution.fsm.view;

import fr.keyser.evolution.model.AttackViolation;
import fr.keyser.evolution.model.AttackViolationStatus;
import fr.keyser.evolution.model.UsedTrait;

public class AttackViolationView {

	private final String type;

	private final UsedTraitView trait;

	private final UsedTraitView bypass;

	private final AttackViolationStatus status;

	public AttackViolationView(AttackViolation a) {
		this(a.getType(), view(a.getTrait()), a.getStatus(), view(a.getBypass()));
	}

	private static UsedTraitView view(UsedTrait ut) {
		if (ut == null)
			return null;

		return new UsedTraitView(ut);
	}

	public AttackViolationView(String type, UsedTraitView trait, AttackViolationStatus status, UsedTraitView bypass) {
		this.type = type;
		this.trait = trait;
		this.bypass = bypass;
		this.status = status;
	}

	public UsedTraitView getTrait() {
		return trait;
	}

	public String getType() {
		return type;
	}

	public UsedTraitView getBypass() {
		return bypass;
	}

	public AttackViolationStatus getStatus() {
		return status;
	}
}
