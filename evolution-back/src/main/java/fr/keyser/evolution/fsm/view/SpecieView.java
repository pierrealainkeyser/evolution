package fr.keyser.evolution.fsm.view;

import java.util.List;
import java.util.stream.Collectors;

import fr.keyser.evolution.core.Specie;

public class SpecieView {

	private final Specie specie;

	private final boolean owner;

	public SpecieView(Specie specie, boolean owner) {
		this.specie = specie;
		this.owner = owner;
	}

	public String getId() {
		return specie.getId().toString();
	}

	public int getFat() {
		return specie.getFat();
	}

	public int getFood() {
		return specie.getFood();
	}

	public int getPopulation() {
		return specie.getPopulation();
	}

	public int getSize() {
		return specie.getSize();
	}

	public List<CardView> getTraits() {
		return specie.getTraits().stream().map(c -> {
			if (c.getState().isVisible() || owner)
				return new CardView(c);
			else
				return CardView.EMPTY;
		}).collect(Collectors.toList());
	}
}
