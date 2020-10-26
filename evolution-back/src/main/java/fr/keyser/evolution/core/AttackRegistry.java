package fr.keyser.evolution.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import fr.keyser.evolution.event.AttackEvent;
import fr.keyser.evolution.event.Attacked;
import fr.keyser.evolution.event.Quilled;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.UsedTrait;

public class AttackRegistry {

	public final static AttackRegistry INITIAL = new AttackRegistry(Collections.emptySet(), Collections.emptyMap());

	private final Set<SpecieId> attackers;

	private final Map<SpecieId, UsedTrait> quilleds;

	private AttackRegistry(Set<SpecieId> attackers, Map<SpecieId, UsedTrait> quilleds) {
		this.attackers = attackers;
		this.quilleds = quilleds;
	}

	public boolean hasAttacked(SpecieId specieId) {
		return attackers.contains(specieId);
	}

	public Optional<UsedTrait> quilled(SpecieId specieId) {
		return Optional.ofNullable(quilleds.get(specieId));
	}

	public AttackRegistry accept(AttackEvent event) {

		if (event instanceof Attacked) {
			Attacked a = (Attacked) event;

			Set<SpecieId> attackers = new HashSet<>(this.attackers);
			attackers.add(a.getAttacker());
			return new AttackRegistry(attackers, quilleds);
		} else if (event instanceof Quilled) {
			Quilled q = (Quilled) event;
			Map<SpecieId, UsedTrait> quilleds = new HashMap<>(this.quilleds);
			quilleds.put(q.getSrc(), q.getTrait());
			return new AttackRegistry(attackers, quilleds);
		}

		return this;
	}
}
