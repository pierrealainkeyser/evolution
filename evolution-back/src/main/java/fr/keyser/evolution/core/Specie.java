package fr.keyser.evolution.core;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.event.FatMoved;
import fr.keyser.evolution.event.FoodEaten;
import fr.keyser.evolution.event.FoodScored;
import fr.keyser.evolution.event.PopulationChanged;
import fr.keyser.evolution.event.SizeIncreased;
import fr.keyser.evolution.event.TraitAdded;
import fr.keyser.evolution.event.TraitsRevealed;
import fr.keyser.evolution.model.FoodConsumption;
import fr.keyser.evolution.model.FoodSource;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.Trait;
import fr.keyser.evolution.model.UsedTrait;

public class Specie {

	@JsonProperty
	private final SpecieId id;

	@JsonProperty
	private final int position;

	@JsonProperty
	private final SpecieStat stat;

	@JsonProperty
	private final Traits traits;

	public Specie(SpecieId id, int position) {
		this(id, position, Traits.INITIAL, SpecieStat.INITIAL);
	}

	@JsonCreator
	public Specie(@JsonProperty("id") SpecieId id, @JsonProperty("position") int position,
			@JsonProperty("traits") Traits traits, @JsonProperty("stat") SpecieStat stat) {
		this.id = id;
		this.position = position;
		this.traits = traits;
		this.stat = stat;
	}

	public Optional<Card> traitAt(int index) {
		return traits.at(index);
	}

	public Optional<TraitsRevealed> handleTraitsRevealed() {
		Map<Integer, Card> cardsToReveal = traits.cardsToReveal();
		if (cardsToReveal.isEmpty())
			return Optional.empty();
		else
			return Optional.of(new TraitsRevealed(id, cardsToReveal));
	}

	@Override
	public String toString() {
		return String.format("Specie[id=%s, position=%s, %s, traits=%s]", id, position, stat, traits.getTraits());
	}

	public int accumulatedFood() {
		return stat.accumulatedFood();
	}

	public Specie traitsRevealed(TraitsRevealed traitsRevealed) {
		return new Specie(id, position, traits.revealed(traitsRevealed), stat);
	}

	public Specie changeSize(SizeIncreased changed) {
		return new Specie(id, position, traits, stat.size(changed.getTo()));
	}

	public Specie changePopulation(PopulationChanged changed) {
		return new Specie(id, position, traits, stat.population(changed.getTo()));
	}

	public Specie traitAdded(TraitAdded added) {
		return new Specie(id, position, traits.added(added), stat);
	}

	public int consumable(FoodSource source, int batchSize) {
		return stat.consumable(traits, source, batchSize);
	}

	public FoodConsumption consumption(int eaten) {
		return stat.consumption(traits, eaten);
	}

	public Specie score(FoodScored scored) {
		return new Specie(id, position, traits, stat.score());
	}

	public Specie eat(FoodEaten eaten) {
		return new Specie(id, position, traits, stat.eat(eaten.getConsumption()));
	}

	@JsonIgnore
	public int getFat() {
		return stat.getFat();
	}

	@JsonIgnore
	public int getFood() {
		return stat.getFood();
	}

	public SpecieId getId() {
		return id;
	}

	@JsonIgnore
	public int getPopulation() {
		return stat.getPopulation();
	}

	public int getPosition() {
		return position;
	}

	@JsonIgnore
	public int getSize() {
		return stat.getSize();
	}

	public int hungryNess() {
		return stat.hungryNess(traits);
	}

	@JsonIgnore
	public List<Card> getTraits() {
		return traits.getTraits();
	}

	public Specie moveFat(FatMoved moved) {
		return new Specie(id, position, traits, stat.moveFat(moved.getFat()));
	}

	public UsedTrait usedTrait(Trait trait) {
		return new UsedTrait(id, trait);
	}

	@JsonIgnore
	public boolean isFed() {
		return stat.isFed();
	}

	// --------------------------------------------------

	@JsonIgnore
	public boolean isHorns() {
		return traits.isHorns();
	}

	@JsonIgnore
	public boolean isQuills() {
		return traits.isQuills();
	}

	@JsonIgnore
	public boolean isVenom() {
		return traits.isVenom();
	}

	@JsonIgnore
	public boolean isCooperation() {
		return traits.isCooperation();
	}

	@JsonIgnore
	public boolean isFatTissue() {
		return traits.isFatTissue();
	}

	@JsonIgnore
	public boolean isFertile() {
		return traits.isFertile();
	}

	@JsonIgnore
	public boolean isForaging() {
		return traits.isForaging();
	}

	@JsonIgnore
	public boolean isLongNeck() {
		return traits.isLongNeck();
	}

	@JsonIgnore
	public boolean isScavenger() {
		return traits.isScavenger();
	}

	@JsonIgnore
	public boolean isIntelligent() {
		return traits.isIntelligent();
	}

	@JsonIgnore
	public boolean isCarnivorous() {
		return traits.isCarnivorous();
	}

	@JsonIgnore
	public boolean isAmbush() {
		return traits.isAmbush();
	}

	@JsonIgnore
	public boolean isBurrowing() {
		return traits.isBurrowing();
	}

	@JsonIgnore
	public boolean isClimbing() {
		return traits.isClimbing();
	}

	@JsonIgnore
	public boolean isHardShell() {
		return traits.isHardShell();
	}

	@JsonIgnore
	public boolean isDefensiveHerding() {
		return traits.isDefensiveHerding();
	}

	@JsonIgnore
	public boolean isSymbiosis() {
		return traits.isSymbiosis();
	}

	@JsonIgnore
	public boolean isWarningCall() {
		return traits.isWarningCall();
	}

	@JsonIgnore
	public boolean isPackHunting() {
		return traits.isPackHunting();
	}

	@JsonIgnore
	public boolean isPest() {
		return traits.isPest();
	}
}
