package fr.keyser.evolution.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.event.Attacked;
import fr.keyser.evolution.event.CardDealed;
import fr.keyser.evolution.event.DeckEvent;
import fr.keyser.evolution.event.DiscardedEvent;
import fr.keyser.evolution.event.PoolRevealed;
import fr.keyser.evolution.event.SpecieExtincted;
import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.CardState;
import fr.keyser.evolution.model.MetaCard;

public class Deck {

	public static final Deck INITIAL = new Deck(Collections.emptyMap(), Collections.emptyList(),
			Collections.emptyList());

	public static final Deck create(Map<CardId, MetaCard> cards) {
		return new Deck(cards, new ArrayList<>(cards.keySet()), Collections.emptyList());
	}

	public class Picker {
		private boolean pickerShuffled;

		private final Queue<CardId> toDrafts;

		public Picker() {
			this.toDrafts = new LinkedList<>(cards);
		}

		public List<CardId> getCards() {
			return new ArrayList<>(toDrafts);
		}

		public boolean isShuffled() {
			return pickerShuffled;
		}

		public List<Card> pick(int count) {
			pickerShuffled = false;

			List<Card> out = new ArrayList<>(count);
			for (int i = 0; i < count; ++i) {
				if (toDrafts.isEmpty()) {
					pickerShuffled = true;
					List<CardId> toShuffle = new ArrayList<>(discarded);
					Collections.shuffle(toShuffle);
					toDrafts.addAll(toShuffle);
				}

				out.add(resolve(toDrafts.poll()));
			}

			return out;
		}
	}

	@JsonProperty
	private final Map<CardId, MetaCard> meta;

	private final List<CardId> cards;

	private final List<CardId> discarded;

	@JsonCreator
	public Deck(@JsonProperty("meta") Map<CardId, MetaCard> meta, @JsonProperty("cards") List<CardId> cards,
			@JsonProperty("discarded") List<CardId> discarded) {
		this.meta = Collections.unmodifiableMap(meta);
		this.cards = Collections.unmodifiableList(cards);
		this.discarded = Collections.unmodifiableList(discarded);
	}

	private Card resolve(CardId id) {
		MetaCard metaCard = getMeta(id);

		return new Card(id, metaCard, CardState.INITIAL);
	}

	public MetaCard getMeta(CardId id) {
		return meta.get(id);
	}

	public Deck accept(DeckEvent event) {
		if (event instanceof CardDealed)
			return dealed((CardDealed) event);
		else if (event instanceof DiscardedEvent)
			return discarded((DiscardedEvent) event);
		else if (event instanceof Attacked)
			return discarded((Attacked) event);
		else if (event instanceof SpecieExtincted)
			return extincted((SpecieExtincted) event);
		else if (event instanceof PoolRevealed)
			return poolRevealed((PoolRevealed) event);

		return this;
	}

	private Deck poolRevealed(PoolRevealed revealed) {
		return discardAll(revealed.getCards().stream().map(Card::getId).collect(Collectors.toList()));
	}

	private Deck dealed(CardDealed evt) {

		if (evt.isShuffle()) {
			return new Deck(meta, evt.getShuffledCards(), Collections.emptyList());
		}

		int skip = evt.getCards().size();
		List<CardId> cards = this.cards.stream().skip(skip).collect(Collectors.toList());
		return new Deck(meta, cards, discarded);
	}

	private Deck extincted(SpecieExtincted evt) {
		List<Card> cards = evt.getTraits();
		if (cards.isEmpty())
			return this;
		else
			return discardAll(cards.stream().map(Card::getId).collect(Collectors.toList()));

	}

	private Deck discarded(Attacked evt) {
		Set<CardId> discardeds = evt.getDiscardeds();
		if (discardeds.isEmpty())
			return this;
		return discardAll(discardeds);
	}

	private Deck discarded(DiscardedEvent evt) {
		CardId id = evt.getDiscarded();
		if (id == null)
			return this;

		return discardAll(Collections.singletonList(id));

	}

	private Deck discardAll(Collection<CardId> all) {
		List<CardId> discarded = new ArrayList<>(this.discarded);
		discarded.addAll(all);
		return new Deck(meta, cards, discarded);
	}

	public List<CardId> getCards() {
		return cards;
	}

	public List<CardId> getDiscarded() {
		return discarded;
	}

	public Picker picker() {
		return new Picker();
	}

}
