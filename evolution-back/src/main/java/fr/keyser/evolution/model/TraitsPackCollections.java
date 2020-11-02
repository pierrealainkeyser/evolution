package fr.keyser.evolution.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.keyser.evolution.core.Deck;

public class TraitsPackCollections {

	private final Map<Trait, TraitsPack> packs = new LinkedHashMap<>();

	public static TraitsPackCollections createDefault() {
		TraitsPackCollections c = new TraitsPackCollections();

		c.add(new TraitsPack(Trait.CARNIVOROUS, -3, -3, -2, -2, -1, -1, 0, 0, 1, 1, 2, 2, 3, 3));
		c.add(new TraitsPack(Trait.VENOM, -3, -2, -1, 0, 0, 1, 2));
		c.add(new TraitsPack(Trait.AMBUSH, -3, -2, -1, 0, 0, 1, 2));
		c.add(new TraitsPack(Trait.PACK_HUNTING, -3, -2, -1, 0, 0, 1, 2));

		c.add(new TraitsPack(Trait.CLIMBING, 0, 1, 2, 3, 4, 5, 6));
		c.add(new TraitsPack(Trait.INTELLIGENT, -2, -2, -1, 0, 1, 2, 2));

		c.add(new TraitsPack(Trait.WARNING_CALL, 0, 1, 2, 2, 3, 3, 3));
		c.add(new TraitsPack(Trait.BURROWING, 2, 3, 3, 4, 5, 5, 5));
		c.add(new TraitsPack(Trait.DEFENSIVE_HERDING, 2, 3, 3, 4, 5, 5, 5));
		c.add(new TraitsPack(Trait.SYMBIOSIS, 2, 3, 3, 4, 5, 5, 5));
		c.add(new TraitsPack(Trait.HORNS, 1, 1, 2, 2, 3, 3, 3));
		c.add(new TraitsPack(Trait.QUILLS, 1, 1, 2, 2, 3, 3, 3));
		c.add(new TraitsPack(Trait.HARD_SHELL, 1, 1, 2, 2, 3, 3, 3));

		c.add(new TraitsPack(Trait.FERTILE, 2, 2, 3, 3, 4, 4, 5));

		c.add(new TraitsPack(Trait.PEST, -3, -2, -1, 0, 0, 1, 2));
		c.add(new TraitsPack(Trait.FAT_TISSUE, 3, 3, 4, 4, 5, 6, 7));
		c.add(new TraitsPack(Trait.LONGNECK, 3, 3, 4, 4, 5, 6, 7));
		c.add(new TraitsPack(Trait.FORAGING, 3, 3, 4, 4, 5, 6, 7));
		c.add(new TraitsPack(Trait.COOPERATION, 3, 3, 4, 4, 5, 6, 7));
		c.add(new TraitsPack(Trait.SCAVENGER, 1, 2, 2, 2, 3, 3, 3));

		return c;
	}

	public Deck create(EvolutionGameSettings parameters) {
		List<Trait> traits = parameters.isCustomTraits() ? parameters.getTraits() : Trait.core();
		return create(traits);
	}

	public Deck create(List<Trait> traits) {
		List<MetaCard> metas = traits.stream().flatMap(t -> packs.get(t).getCards().stream())
				.collect(Collectors.toList());
		Collections.shuffle(metas);

		int seq = 0;
		Map<CardId, MetaCard> withId = new LinkedHashMap<>();
		for (MetaCard meta : metas)
			withId.put(new CardId(seq++), meta);

		return Deck.create(withId);
	}

	void add(TraitsPack pack) {
		this.packs.put(pack.getTrait(), pack);
	}

}
