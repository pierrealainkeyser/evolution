package fr.keyser.evolution.summary;

import java.util.List;

import fr.keyser.evolution.model.AttackViolation;
import fr.keyser.evolution.model.SpecieId;

public class AttackSummary implements Summary {
	private final SpecieId specie;

	private final SpecieId target;

	private final List<AttackViolation> violations;

	private final List<AttackOutcome> outcomes;

	public AttackSummary(SpecieId specie, SpecieId target, List<AttackViolation> violations,
			List<AttackOutcome> outcomes) {
		this.specie = specie;
		this.target = target;
		this.violations = violations;
		this.outcomes = outcomes;
	}

	public boolean isPossible() {
		return !outcomes.isEmpty();
	}

	@Override
	public SpecieId getSpecie() {
		return specie;
	}

	public SpecieId getTarget() {
		return target;
	}

	public List<AttackViolation> getViolations() {
		return violations;
	}

	public List<AttackOutcome> getOutcomes() {
		return outcomes;
	}

	@Override
	public String toString() {
		return String.format("AttackSummary [specie=%s, target=%s, violations=%s, outcomes=%s]", specie, target,
				violations, outcomes);
	}

}
