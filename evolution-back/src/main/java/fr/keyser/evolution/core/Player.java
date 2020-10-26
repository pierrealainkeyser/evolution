package fr.keyser.evolution.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import fr.keyser.evolution.command.AddCardToPoolCommand;
import fr.keyser.evolution.command.AddSpeciesCommand;
import fr.keyser.evolution.command.AddTraitCommand;
import fr.keyser.evolution.command.IncreasePopulationCommand;
import fr.keyser.evolution.command.IncreaseSizeCommand;
import fr.keyser.evolution.event.Attacked;
import fr.keyser.evolution.event.CardAddedToPool;
import fr.keyser.evolution.event.CardDealed;
import fr.keyser.evolution.event.DiscardedEvent;
import fr.keyser.evolution.event.PopulationIncreased;
import fr.keyser.evolution.event.Scored;
import fr.keyser.evolution.event.SizeIncreased;
import fr.keyser.evolution.event.SpecieAdded;
import fr.keyser.evolution.event.TraitAdded;
import fr.keyser.evolution.model.Card;
import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;

public class Player {

	public static final Player id(int id) {
		return new Player(id, 0, Collections.emptyList());
	}

	private final List<Card> hands;

	private final int id;

	private final int score;

	private Player(int id, int score, List<Card> hands) {
		this.id = id;
		this.score = score;
		this.hands = Collections.unmodifiableList(hands);
	}

	public CardId checkCard(CardId card) {
		return inHand(card).getId();
	}

	public SizeIncreased handleIncreaseSize(IncreaseSizeCommand command, Specie target) {
		int size = target.getSize() + 1;
		if (size > 6)
			throw new IllegalArgumentException("size too big");
		return new SizeIncreased(target.getId(), size, checkCard(command.getCard()));
	}

	public PopulationIncreased handleIncreasePopulation(IncreasePopulationCommand command, Specie target) {
		int population = target.getPopulation() + 1;
		if (population > 6)
			throw new IllegalArgumentException("population too big");
		return new PopulationIncreased(target.getId(), population, checkCard(command.getCard()));
	}

	public TraitAdded handleAddTrait(AddTraitCommand command, Specie target) {
		int position = command.getPosition();
		return new TraitAdded(target.getId(), inHand(command.getCard()), position,
				target.traitAt(position).orElse(null));
	}

	public SpecieAdded handleAddSpecie(AddSpeciesCommand command, SpecieId target) {
		return new SpecieAdded(target, command.getPosition(), checkCard(command.getCard()));
	}

	public CardAddedToPool handleAddToPool(AddCardToPoolCommand command) {
		return new CardAddedToPool(this.id, inHand(command.getCard()));
	}

	private Card inHand(CardId id) {
		return hands.stream().filter(c -> c.getId().equals(id)).findFirst().orElseThrow();
	}

	public Player addCards(CardDealed dealed) {
		List<Card> hands = new ArrayList<>(this.hands);
		hands.addAll(dealed.getCards());
		return new Player(id, score, hands);
	}

	public Player discard(Attacked event) {
		Set<CardId> discardeds = event.getDiscardeds();
		if (discardeds.isEmpty())
			return this;

		return new Player(id, score,
				hands.stream().filter(c -> !discardeds.contains(c.getId())).collect(Collectors.toList()));

	}

	public Player discard(DiscardedEvent event) {
		CardId discarded = event.getDiscarded();
		if (discarded == null)
			return this;

		return new Player(id, score,
				hands.stream().filter(c -> !c.getId().equals(discarded)).collect(Collectors.toList()));
	}

	public int getHandSize() {
		return hands.size();
	}

	public List<Card> getHands() {
		return hands;
	}

	public int getId() {
		return id;
	}

	public int getScore() {
		return score;
	}

	public Player score(Scored scored) {
		return new Player(id, score + scored.getScore(), hands);
	}

	@Override
	public String toString() {
		return String.format("Player [id=%s, score=%s, hands=%s]", id, score, hands);
	}
}
