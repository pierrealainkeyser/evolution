package fr.keyser.evolution.command;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.core.Player;
import fr.keyser.evolution.event.Attacked;
import fr.keyser.evolution.exception.IllegalCommandException;
import fr.keyser.evolution.model.AttackViolationStatus;
import fr.keyser.evolution.model.AttackViolations;
import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.DisabledViolation;
import fr.keyser.evolution.model.SpecieId;

public class AttackCommand extends FeedingPhaseCommand {

	private final SpecieId target;

	private final Map<String, CardId> violations;

	public AttackCommand(AttackViolations violations, Map<String, CardId> resolved) {
		this(violations.getSource(), violations.getTarget(), resolved);
	}

	@JsonCreator
	public AttackCommand(@JsonProperty("specie") SpecieId specie, @JsonProperty("target") SpecieId target,
			@JsonProperty("violations") Map<String, CardId> violations) {
		super(specie);
		this.target = target;
		this.violations = violations;
	}

	public Attacked resolve(AttackViolations attack, Player player, AttackCommand cmd) {

		List<DisabledViolation> disabled = attack.getViolations().stream().flatMap(t -> {
			String key = t.getType();

			CardId discarded = null;
			AttackViolationStatus status = t.getStatus();
			if (status.isPayable()) {
				discarded = violations.get(key);
				if (discarded == null) {
					if (status.isPayRequired())
						throw new IllegalCommandException(key + " is required", cmd);
					return Stream.empty();
				}
			} else if (status.isBlocked())
				throw new IllegalCommandException(key + " is blocked", cmd);
			else if (status.isAccepted())
				return Stream.empty();

			return Stream.of(new DisabledViolation(key, t.getTrait(), t.getBypass(),
					discarded != null ? player.inHand(discarded) : null));

		}).collect(Collectors.toList());

		return new Attacked(target, getSpecie(), disabled);
	}

	public SpecieId getTarget() {
		return target;
	}

	public Map<String, CardId> getViolations() {
		return violations;
	}

}
