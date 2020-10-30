package fr.keyser.evolution.model;

import java.util.List;
import java.util.stream.Collectors;

public class AttackViolations {

	private final SpecieId source;

	private final SpecieId target;

	private final List<AttackViolation> violations;

	public AttackViolations(SpecieId source, SpecieId target, List<AttackViolation> violations) {
		this.source = source;
		this.target = target;
		this.violations = violations;
	}

	public boolean isPossible(int handSize) {

		boolean blocked = violations.stream().anyMatch(a -> a.getStatus() == AttackViolationStatus.BLOCK);
		if (blocked)
			return false;

		int must = violations.stream().mapToInt(a -> a.getStatus() == AttackViolationStatus.MUST_PAY ? 1 : 0).sum();
		return must <= handSize;
	}

	public List<String> getMustPay() {
		return violations.stream().filter(a -> a.getStatus() == AttackViolationStatus.MUST_PAY)
				.map(AttackViolation::getType).collect(Collectors.toList());
	}

	public List<String> getMayPay() {
		return violations.stream().filter(a -> a.getStatus() == AttackViolationStatus.MAY_PAY)
				.map(AttackViolation::getType).collect(Collectors.toList());
	}

	public SpecieId getTarget() {
		return target;
	}

	public List<AttackViolation> getViolations() {
		return violations;
	}

	public SpecieId getSource() {
		return source;
	}
}
