package fr.keyser.evolution.core;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import fr.keyser.evolution.event.FoodEaten;
import fr.keyser.evolution.event.SpecieAdded;
import fr.keyser.evolution.event.SpecieExtincted;
import fr.keyser.evolution.model.FoodSource;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.SpeciePosition;

public class TestSpecies {

	@Test
	void nominal() {
		Species species = Species.INITIAL;

		species = species.accept(new SpecieAdded(species.newId(0), SpeciePosition.LEFT, null));
		species = species.accept(new SpecieAdded(species.newId(0), SpeciePosition.RIGHT, null));
		species = species.accept(new SpecieAdded(species.newId(1), SpeciePosition.RIGHT, null));
		species = species.accept(new SpecieAdded(species.newId(1), SpeciePosition.RIGHT, null));

		species.stream().forEach(System.out::println);

		Specie first = species.stream().findFirst().get();
		FoodEaten eaten = new FoodEaten(first.getId(),
				first.consumption(first.consumable(FoodSource.POOL, 1)),
				FoodSource.POOL, Collections.emptyList(), null);

		species = species.accept(eaten);
		species = species.accept(new SpecieExtincted(new SpecieId(2, 0), 0, null));
		species.stream().forEach(System.out::println);

	}
}
