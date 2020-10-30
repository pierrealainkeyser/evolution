package fr.keyser.evolution.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CardState {

	private final boolean visible;

	private final boolean preview;

	public static final CardState TRAIT_INITIAL = new CardState(false, true);

	public static final CardState INITIAL = new CardState(false, true);

	public static final CardState REVEAL = new CardState(true, false);

	public static final CardState POOL = new CardState(false, false);

	@JsonCreator
	public CardState(@JsonProperty("visible") boolean visible, @JsonProperty("preview") boolean preview) {
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
