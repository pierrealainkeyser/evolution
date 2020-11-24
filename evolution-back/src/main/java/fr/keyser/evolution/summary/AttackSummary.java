package fr.keyser.evolution.summary;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.model.AttackViolation;
import fr.keyser.evolution.model.SpecieId;

public class AttackSummary implements FeedingActionSummary {
	private final SpecieId specie;

	private final SpecieId target;

	private final List<AttackViolation> violations;

	private final List<AttackOutcome> outcomes;
	
	private final boolean optional;

	@JsonCreator
	public AttackSummary(@JsonProperty("optional") boolean optional,@JsonProperty("specie") SpecieId specie, @JsonProperty("target") SpecieId target,
			@JsonProperty("violations") List<AttackViolation> violations,
			@JsonProperty("outcomes") List<AttackOutcome> outcomes) {
		this.optional=optional;
		this.specie = specie;
		this.target = target;
		this.violations = violations;
		this.outcomes = outcomes;
	}

	@JsonIgnore
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
		return String.format("AttackSummaryView [specie=%s, target=%s, violations=%s, outcomes=%s]", specie, target,
				violations, outcomes);
	}

	@Override
	public boolean isOptional() {
		return optional;
	}

}
