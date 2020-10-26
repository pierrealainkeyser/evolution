package fr.keyser.evolution.event;

import fr.keyser.evolution.model.CardId;

public interface DiscardedEvent extends PlayerEvent, DeckEvent {
	public CardId getDiscarded();

}
