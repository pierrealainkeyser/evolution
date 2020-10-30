package fr.keyser.evolution.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;

import fr.keyser.evolution.event.FatMoved;
import fr.keyser.evolution.event.FoodEaten;
import fr.keyser.evolution.event.FoodScored;
import fr.keyser.evolution.event.PopulationChanged;
import fr.keyser.evolution.event.SizeIncreased;
import fr.keyser.evolution.event.SpecieAdded;
import fr.keyser.evolution.event.SpecieEvent;
import fr.keyser.evolution.event.SpecieExtincted;
import fr.keyser.evolution.event.TraitAdded;
import fr.keyser.evolution.event.TraitsRevealed;
import fr.keyser.evolution.model.PlayerSpecies;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.SpeciePosition;

public class Species {

	public static final Species INITIAL = new Species(Collections.emptyMap());

	private final Map<SpecieId, Specie> species;

	@JsonCreator
	public Species(@JsonUnwrapped List<Specie> species) {
		this(species.stream().collect(Collectors.toMap(Specie::getId, Function.identity())));
	}

	protected Species(Map<SpecieId, Specie> species) {
		this.species = Collections.unmodifiableMap(species);
	}

	@JsonValue
	public List<Specie> values() {
		return new ArrayList<>(species.values());
	}

	@Override
	public String toString() {
		return species.values().stream().map(Specie::toString).collect(Collectors.joining(", "));
	}

	public Species accept(SpecieEvent event) {
		if (event instanceof FoodEaten) {
			return this.process((FoodEaten) event, Specie::eat);
		} else if (event instanceof FoodScored) {
			return this.process((FoodScored) event, Specie::score);
		} else if (event instanceof FatMoved) {
			return this.process((FatMoved) event, Specie::moveFat);
		} else if (event instanceof TraitAdded) {
			return this.process((TraitAdded) event, Specie::traitAdded);
		} else if (event instanceof PopulationChanged) {
			return this.process((PopulationChanged) event, Specie::changePopulation);
		} else if (event instanceof SizeIncreased) {
			return this.process((SizeIncreased) event, Specie::changeSize);
		} else if (event instanceof TraitsRevealed) {
			return this.process((TraitsRevealed) event, Specie::traitsRevealed);
		} else if (event instanceof SpecieExtincted) {
			return extincted((SpecieExtincted) event);
		} else if (event instanceof SpecieAdded) {
			return added((SpecieAdded) event);
		}

		return this;
	}

	public SpecieId newId(int player) {
		return new SpecieId(species.keySet().stream().mapToInt(SpecieId::getId).max().orElse(-1) + 1,
				player);
	}

	private Species added(SpecieAdded added) {

		int player = added.getPlayer();
		SpecieId specieId = new SpecieId(species.keySet().stream().mapToInt(SpecieId::getId).max().orElse(-1) + 1,
				player);
		PlayerSpecies forPlayer = forPlayer(player);

		int position = 0;
		if (SpeciePosition.LEFT == added.getPosition())
			position = forPlayer.leftPosition();
		else
			position = forPlayer.rightPosition();

		Specie specie = new Specie(specieId, position);

		Map<SpecieId, Specie> newSpecies = copy();
		newSpecies.put(specie.getId(), specie);
		return new Species(newSpecies);

	}

	private Map<SpecieId, Specie> copy() {
		return new LinkedHashMap<>(species);
	}

	private Species extincted(SpecieExtincted exincted) {
		Map<SpecieId, Specie> newSpecies = copy();
		newSpecies.remove(exincted.getSrc());
		return new Species(newSpecies);
	}

	public PlayerSpecies forPlayer(int player) {
		List<Specie> list = species.values().stream()
				.filter(s -> s.getId().getPlayer() == player)
				.sorted(Comparator.comparing(Specie::getPosition))
				.collect(Collectors.toList());

		return new PlayerSpecies(list);
	}

	public Optional<Specie> rightOf(Specie src) {
		return forPlayer(src.getId().getPlayer()).rightOf(src);
	}

	public Specie forEvent(SpecieEvent event) {
		return byId(event.getSrc());
	}

	public Specie byId(SpecieId src) {
		return species.get(src);
	}

	private <E extends SpecieEvent> Species process(E e, BiFunction<Specie, E, Specie> action) {
		SpecieId src = e.getSrc();
		Specie specie = species.get(src);

		Map<SpecieId, Specie> newSpecies = copy();
		newSpecies.put(src, action.apply(specie, e));
		return new Species(newSpecies);
	}

	public Stream<Specie> stream() {
		return species.values().stream();
	}
}
