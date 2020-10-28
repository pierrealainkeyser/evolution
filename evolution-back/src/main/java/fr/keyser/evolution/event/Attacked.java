package fr.keyser.evolution.event;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import fr.keyser.evolution.model.Card;
import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.DisabledViolation;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.Trait;

public class Attacked extends SpecieEvent implements DeckEvent, PlayerEvent, AttackEvent {

	private final SpecieId attacker;

	private final List<DisabledViolation> disabled;

	public Attacked(SpecieId src, SpecieId attacker, List<DisabledViolation> disabled) {
		super(src);
		this.attacker = attacker;
		this.disabled = disabled;
	}

	public SpecieId getAttacker() {
		return attacker;
	}

	public Set<CardId> getDiscardeds() {
		return disabled.stream().flatMap(d -> Optional.ofNullable(d.getDiscarded()).map(Card::getId).stream())
				.collect(Collectors.toSet());
	}

	public List<DisabledViolation> getDisabled() {
		return disabled;
	}

	public boolean traitDisabled(Trait t) {
		return disabled.stream().anyMatch(d -> d.match(t));
	}

	@Override
	public int getPlayer() {
		return attacker.getPlayer();
	}

	@Override
	public String toString() {
		return String.format("Attacked [attacker=%s, disabled=%s]", attacker, disabled);
	}

}
