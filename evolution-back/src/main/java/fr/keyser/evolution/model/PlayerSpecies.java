package fr.keyser.evolution.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Stream;

import fr.keyser.evolution.core.Specie;

public class PlayerSpecies {

	private final List<Specie> species;

	public PlayerSpecies(List<Specie> species) {
		this.species = Collections.unmodifiableList(species);
	}

	public int leftPosition() {
		OptionalInt pos = species.stream().mapToInt(Specie::getPosition).min();
		if (pos.isPresent())
			return pos.getAsInt() - 1;
		else
			return 0;
	}

	public Stream<Specie> stream() {
		return species.stream();
	}

	public int size() {
		return species.size();
	}

	public int rightPosition() {
		OptionalInt pos = species.stream().mapToInt(Specie::getPosition).max();
		if (pos.isPresent())
			return pos.getAsInt() + 1;
		else
			return 0;
	}

	public Optional<Specie> rightOf(Specie src) {
		SpecieId id = src.getId();
		for (int i = 0, size = species.size(); i < size; ++i) {
			Specie spec = species.get(i);
			if (spec.getId().equals(id)) {
				if (i < size - 1)
					return Optional.of(species.get(i + 1));
				else {
					break;
				}
			}
		}
		return Optional.empty();
	}

	public Optional<Specie> leftOf(Specie src) {
		SpecieId id = src.getId();
		for (int i = 0, size = species.size(); i < size; ++i) {
			Specie spec = species.get(i);
			if (spec.getId().equals(id)) {
				if (i > 0)
					return Optional.of(species.get(i - 1));
				else {
					break;
				}
			}
		}
		return Optional.empty();
	}
}
