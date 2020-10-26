package fr.keyser.evolution.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import fr.keyser.evolution.event.TraitAdded;
import fr.keyser.evolution.event.TraitsRevealed;
import fr.keyser.evolution.model.Card;
import fr.keyser.evolution.model.CardState;
import fr.keyser.evolution.model.Trait;

public class Traits {

	public static final Traits INITIAL = new Traits(Collections.emptyList());

	private final List<Card> traits;

	private Traits(List<Card> traits) {
		this.traits = Collections.unmodifiableList(traits);
	}

	public Traits added(TraitAdded added) {
		List<Card> traits = new ArrayList<>(this.traits);
		int index = added.getIndex();
		Card card = added.getCard().state(CardState::traitInitial);
		if (index >= traits.size())
			traits.add(card);
		else
			traits.set(index, card);

		return new Traits(traits);
	}

	public Traits revealed(TraitsRevealed revealed) {
		List<Card> traits = new ArrayList<>(this.traits);
		Map<Integer, Card> visible = revealed.getTraits();
		for (int i = 0, size = traits.size(); i < size; ++i) {
			Card card = visible.get(i);
			if (card != null)
				traits.set(i, card);
		}
		return new Traits(traits);
	}

	public Optional<Card> at(int index) {
		if (index >= traits.size())
			return Optional.empty();
		else
			return Optional.of(traits.get(index));

	}

	public Map<Integer, Card> cardsToReveal() {
		Map<Integer, Card> reveal = new HashMap<>();
		for (int i = 0, size = traits.size(); i < size; ++i) {
			Card c = traits.get(i);
			if (!c.getState().isVisible())
				reveal.put(i, c.state(CardState::reveal));

		}

		return reveal;
	}

	public boolean contains(Trait trait) {
		return traits.stream().anyMatch(c -> c.getMeta().getTrait() == trait);
	}

	public List<Card> getTraits() {
		return traits;
	}

	public Set<Trait> asTraits() {
		return traits.stream().map(c -> c.getMeta().getTrait()).collect(Collectors.toSet());
	}

	// ---------------------------

	public boolean isHorns() {
		return contains(Trait.HORNS);
	}

	public boolean isQuills() {
		return contains(Trait.QUILLS);
	}

	public boolean isVenom() {
		return contains(Trait.VENOM);
	}

	public boolean isLongNeck() {
		return contains(Trait.LONGNECK);
	}

	public boolean isCooperation() {
		return contains(Trait.COOPERATION);
	}

	public boolean isFertile() {
		return contains(Trait.FERTILE);
	}

	public boolean isIntelligent() {
		return contains(Trait.INTELLIGENT);
	}

	public boolean isFatTissue() {
		return contains(Trait.FAT_TISSUE);
	}

	public boolean isForaging() {
		return contains(Trait.FORAGING);
	}

	public boolean isScavenger() {
		return contains(Trait.SCAVENGER);
	}

	public boolean isCarnivorous() {
		return contains(Trait.CARNIVOROUS);
	}

	public boolean isAmbush() {
		return contains(Trait.AMBUSH);
	}

	public boolean isBurrowing() {
		return contains(Trait.BURROWING);
	}

	public boolean isClimbing() {
		return contains(Trait.CLIMBING);
	}

	public boolean isPest() {
		return contains(Trait.PEST);
	}

	public boolean isHardShell() {
		return contains(Trait.HARD_SHELL);
	}

	public boolean isDefensiveHerding() {
		return contains(Trait.DEFENSIVE_HERDING);
	}

	public boolean isSymbiosis() {
		return contains(Trait.SYMBIOSIS);
	}

	public boolean isWarningCall() {
		return contains(Trait.WARNING_CALL);
	}

	public boolean isPackHunting() {
		return contains(Trait.PACK_HUNTING);
	}

}
