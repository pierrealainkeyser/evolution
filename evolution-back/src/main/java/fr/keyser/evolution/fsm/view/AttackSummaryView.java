package fr.keyser.evolution.fsm.view;

import java.util.List;

import fr.keyser.evolution.model.SpecieId;

public class AttackSummaryView implements SummaryView {
	private final SpecieId specie;

	private final SpecieId target;

	private final List<AttackViolationView> violations;

	private final List<AttackOutcomeView> outcomes;

	public AttackSummaryView(SpecieId specie, SpecieId target, List<AttackViolationView> violations,
			List<AttackOutcomeView> outcomes) {
		this.specie = specie;
		this.target = target;
		this.violations = violations;
		this.outcomes = outcomes;
	}

	@Override
	public String getType() {
		return "attack";
	}

	public boolean isPossible() {
		return !outcomes.isEmpty();
	}

	public SpecieId getSpecie() {
		return specie;
	}

	public SpecieId getTarget() {
		return target;
	}

	public List<AttackViolationView> getViolations() {
		return violations;
	}

	public List<AttackOutcomeView> getOutcomes() {
		return outcomes;
	}
}
