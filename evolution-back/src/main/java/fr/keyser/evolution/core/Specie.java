package fr.keyser.evolution.core;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.keyser.evolution.event.FatMoved;
import fr.keyser.evolution.event.FoodEaten;
import fr.keyser.evolution.event.FoodScored;
import fr.keyser.evolution.event.PopulationChanged;
import fr.keyser.evolution.event.SizeIncreased;
import fr.keyser.evolution.event.TraitAdded;
import fr.keyser.evolution.event.TraitsRevealed;
import fr.keyser.evolution.model.Card;
import fr.keyser.evolution.model.FoodConsumption;
import fr.keyser.evolution.model.FoodSource;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.Trait;
import fr.keyser.evolution.model.UsedTrait;

public class Specie {

	private final SpecieId id;

	private final int position;

	private final SpecieStat stat;

	private final Traits traits;

	public Specie(SpecieId id, int position) {
		this(id, position, Traits.INITIAL, SpecieStat.INITIAL);
	}

	private Specie(SpecieId id, int position, Traits traits, SpecieStat stat) {
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

	public int getFat() {
		return stat.getFat();
	}

	public int getFood() {
		return stat.getFood();
	}

	public SpecieId getId() {
		return id;
	}

	public int getPopulation() {
		return stat.getPopulation();
	}

	public int getPosition() {
		return position;
	}

	public int getSize() {
		return stat.getSize();
	}

	public int hungryNess() {
		return stat.hungryNess(traits);
	}

	public List<Card> getTraits() {
		return traits.getTraits();
	}

	public Specie moveFat(FatMoved moved) {
		return new Specie(id, position, traits, stat.moveFat(moved.getFat()));
	}

	public UsedTrait usedTrait(Trait trait) {
		return new UsedTrait(id, trait);
	}

	public boolean isFed() {
		return stat.isFed();
	}

	// --------------------------------------------------

	public boolean isHorns() {
		return traits.isHorns();
	}

	public boolean isQuills() {
		return traits.isQuills();
	}

	public boolean isVenom() {
		return traits.isVenom();
	}

	public boolean isCooperation() {
		return traits.isCooperation();
	}

	public boolean isFatTissue() {
		return traits.isFatTissue();
	}

	public boolean isFertile() {
		return traits.isFertile();
	}

	public boolean isForaging() {
		return traits.isForaging();
	}

	public boolean isLongNeck() {
		return traits.isLongNeck();
	}

	public boolean isScavenger() {
		return traits.isScavenger();
	}

	public boolean isIntelligent() {
		return traits.isIntelligent();
	}

	public boolean isCarnivorous() {
		return traits.isCarnivorous();
	}

	public boolean isAmbush() {
		return traits.isAmbush();
	}

	public boolean isBurrowing() {
		return traits.isBurrowing();
	}

	public boolean isClimbing() {
		return traits.isClimbing();
	}

	public boolean isHardShell() {
		return traits.isHardShell();
	}

	public boolean isDefensiveHerding() {
		return traits.isDefensiveHerding();
	}

	public boolean isSymbiosis() {
		return traits.isSymbiosis();
	}

	public boolean isWarningCall() {
		return traits.isWarningCall();
	}

	public boolean isPackHunting() {
		return traits.isPackHunting();
	}

	public boolean isPest() {
		return traits.isPest();
	}
}
