package fr.keyser.evolution.fsm.view;

import fr.keyser.evolution.core.Card;
import fr.keyser.evolution.model.DisabledViolation;
import fr.keyser.evolution.model.UsedTrait;

public class DisabledViolationView {

	private final String violation;

	private final UsedTraitView trait;

	private final UsedTraitView used;

	private final CardView discarded;

	public DisabledViolationView(DisabledViolation d) {
		this(d.getViolation(), view(d.getTrait()), view(d.getUsed()), view(d.getDiscarded()));
	}

	private static CardView view(Card c) {
		if (c == null)
			return null;
		return new CardView(c);
	}

	private static UsedTraitView view(UsedTrait ut) {
		if (ut == null)
			return null;

		return new UsedTraitView(ut);
	}

	public DisabledViolationView(String violation, UsedTraitView trait, UsedTraitView used, CardView discarded) {
		this.violation = violation;
		this.trait = trait;
		this.used = used;
		this.discarded = discarded;
	}

	public String getViolation() {
		return violation;
	}

	public UsedTraitView getTrait() {
		return trait;
	}

	public UsedTraitView getUsed() {
		return used;
	}

	public CardView getDiscarded() {
		return discarded;
	}
}
