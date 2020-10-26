package fr.keyser.evolution.model;

public class CardState {

	private final boolean visible;

	private final boolean preview;

	public static final CardState TRAIT_INITIAL = new CardState(false, true);

	public static final CardState INITIAL = new CardState(false, true);

	public static final CardState REVEAL = new CardState(true, false);

	public static final CardState POOL = new CardState(false, false);

	public CardState(boolean visible, boolean preview) {
		this.visible = visible;
		this.preview = preview;
	}

	public CardState traitInitial() {
		return TRAIT_INITIAL;
	}

	public CardState pool() {
		return POOL;
	}

	public CardState reveal() {
		return REVEAL;
	}

	public boolean isVisible() {
		return visible;
	}

	public boolean isPreview() {
		return preview;
	}
}
