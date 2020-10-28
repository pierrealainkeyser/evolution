package fr.keyser.evolution.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.keyser.evolution.command.AttackCommand;
import fr.keyser.evolution.command.Command;
import fr.keyser.evolution.command.FeedCommand;
import fr.keyser.evolution.command.IntelligentFeedCommand;
import fr.keyser.evolution.command.PlayerCommand;
import fr.keyser.evolution.command.SpecieCommand;
import fr.keyser.evolution.core.Deck.Picker;
import fr.keyser.evolution.engine.Event;
import fr.keyser.evolution.engine.EventConsumer;
import fr.keyser.evolution.engine.EventProcessor;
import fr.keyser.evolution.event.AttackEvent;
import fr.keyser.evolution.event.Attacked;
import fr.keyser.evolution.event.CardDealed;
import fr.keyser.evolution.event.DeckEvent;
import fr.keyser.evolution.event.DiscardPoolFood;
import fr.keyser.evolution.event.FatMoved;
import fr.keyser.evolution.event.FoodEaten;
import fr.keyser.evolution.event.FoodScored;
import fr.keyser.evolution.event.LastTurnEvent;
import fr.keyser.evolution.event.PlayerEvent;
import fr.keyser.evolution.event.PoolEvent;
import fr.keyser.evolution.event.PoolRevealed;
import fr.keyser.evolution.event.PopulationGrow;
import fr.keyser.evolution.event.PopulationReduced;
import fr.keyser.evolution.event.Quilled;
import fr.keyser.evolution.event.SpecieAdded;
import fr.keyser.evolution.event.SpecieEvent;
import fr.keyser.evolution.event.SpecieExtincted;
import fr.keyser.evolution.event.TraitsRevealed;
import fr.keyser.evolution.event.TurnEvent;
import fr.keyser.evolution.model.AttackViolation;
import fr.keyser.evolution.model.AttackViolationStatus;
import fr.keyser.evolution.model.AttackViolations;
import fr.keyser.evolution.model.Card;
import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.DisabledViolation;
import fr.keyser.evolution.model.FoodConsumption;
import fr.keyser.evolution.model.FoodSource;
import fr.keyser.evolution.model.PlayerScoreBoard;
import fr.keyser.evolution.model.PlayerSpecies;
import fr.keyser.evolution.model.Score;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.SpeciePosition;
import fr.keyser.evolution.model.Trait;
import fr.keyser.evolution.model.UsedTrait;
import fr.keyser.evolution.summary.AttackOutcome;
import fr.keyser.evolution.summary.AttackSummary;
import fr.keyser.evolution.summary.FeedSummary;
import fr.keyser.evolution.summary.IntelligentFeedSummary;
import fr.keyser.evolution.summary.Summary;

public class PlayArea implements EventProcessor<Event, PlayArea> {

	public static final PlayArea with(Players players, Deck deck) {
		return new PlayArea(players, deck, Species.INITIAL, FoodPool.INITIAL, TurnStatus.INITIAL);
	}

	public static final PlayArea init(Players players, Deck deck) {

		Species species = Species.INITIAL;
		for (int i = 0, size = players.size(); i < size; ++i)
			species = species.accept(new SpecieAdded(species.newId(i), SpeciePosition.LEFT, null));

		return new PlayArea(players, deck, species, FoodPool.INITIAL, TurnStatus.INITIAL);
	}

	private final Players players;

	private final FoodPool pool;

	private final Species species;

	private final Deck deck;

	private final TurnStatus turnStatus;

	private PlayArea(Players players, Deck deck, Species species, FoodPool pool, TurnStatus turnStatus) {
		this.players = players;
		this.deck = deck;
		this.species = species;
		this.pool = pool;
		this.turnStatus = turnStatus;
	}

	private PlayArea accept(Event event) {
		FoodPool pool = this.pool;
		if (event instanceof PoolEvent)
			pool = pool.accept((PoolEvent) event);

		Species species = this.species;
		if (event instanceof SpecieEvent)
			species = species.accept((SpecieEvent) event);

		Players players = this.players;
		if (event instanceof PlayerEvent)
			players = players.accept((PlayerEvent) event);

		Deck deck = this.deck;
		if (event instanceof DeckEvent)
			deck = deck.accept((DeckEvent) event);

		TurnStatus turnStatus = this.turnStatus;
		if (event instanceof TurnEvent || event instanceof AttackEvent)
			turnStatus = turnStatus.accept(event);

		return new PlayArea(players, deck, species, pool, turnStatus);
	}

	private static class CardIdSequence implements Function<String, CardId> {
		private final List<CardId> hands;

		private int index = 0;

		public CardIdSequence(Player player) {
			this.hands = player.getHands().stream().map(Card::getId).collect(Collectors.toList());
		}

		private CardIdSequence(List<CardId> hands, int index) {
			this.hands = hands;
			this.index = index;
		}

		public CardIdSequence reset() {
			return new CardIdSequence(hands, 0);
		}

		@Override
		public CardId apply(String t) {
			return hands.get(index++);
		}
	}

	public List<PlayerScoreBoard> scoreBoards() {

		Map<Integer, Score> scoreBoards = players.getPlayers().stream()
				.collect(Collectors.toMap(Player::getId, p -> {
					int id = p.getId();
					PlayerSpecies spec = species.forPlayer(id);
					int traits = spec.stream().mapToInt(s -> s.getTraits().size()).sum();
					int population = spec.stream().mapToInt(Specie::getPopulation).sum();

					return new Score(p.getScore(), traits, population);
				}));

		Score alpha = scoreBoards.values().stream().max(Score::compareTo).get();
		return scoreBoards.entrySet().stream()
				.map(e -> new PlayerScoreBoard(e.getKey(), e.getValue(), e.getValue().compareTo(alpha) == 0))
				.sorted((p0, p1) -> p1.getScore().compareTo(p0.getScore()))
				.collect(Collectors.toList());
	}

	public List<Summary> actionsForPlayer(int player) {
		Map<Integer, PlayerSpecies> all = players.getPlayers().stream()
				.collect(Collectors.toMap(Player::getId, p -> species.forPlayer(p.getId())));
		PlayerSpecies current = all.get(player);
		return current.stream().flatMap(s -> summaryFor(s, all)).collect(Collectors.toList());
	}

	private Stream<Summary> summaryFor(Specie specie, Map<Integer, PlayerSpecies> all) {

		int hungryNess = specie.hungryNess();
		if (hungryNess > 0) {
			if (specie.isCarnivorous())
				return carnivorousFeeding(specie, all);
			else
				return vegetarianFeeding(specie);
		}

		return Stream.empty();

	}

	private Stream<Summary> carnivorousFeeding(Specie specie, Map<Integer, PlayerSpecies> all) {
		SpecieId id = specie.getId();
		return all.values().stream().flatMap(arround -> {
			return arround.stream().flatMap(target -> {
				if (id.equals(target.getId()))
					return Stream.empty();

				AttackViolations attack = attack(specie, target, arround);
				return Stream.of(summarize(attack));
			});
		});
	}

	private FoodEaten intelligentPlantEat(SpecieId specieId, Card discarded) {
		Specie specie = species.byId(specieId);

		FoodSource source = FoodSource.PLANT;
		List<UsedTrait> traits = new ArrayList<>();
		traits.add(specie.usedTrait(Trait.INTELLIGENT));

		FoodConsumption consumption = specie.consumption(specie.consumable(source, 2));
		feedingUsedTraits(specie, source, consumption, traits);
		return new FoodEaten(specieId, consumption, source, traits, discarded);
	}

	private Optional<FoodEaten> plantEat(SpecieId specieId) {
		Specie specie = species.byId(specieId);

		FoodSource source = FoodSource.POOL;
		int food = Math.min(specie.consumable(source, 1), pool.getFood());
		if (food > 0) {
			List<UsedTrait> traits = new ArrayList<>();
			FoodConsumption consumption = specie.consumption(food);
			feedingUsedTraits(specie, source, consumption, traits);
			return Optional.of(new FoodEaten(specieId, consumption, source, traits, null));
		}
		return Optional.empty();
	}

	private Stream<Summary> vegetarianFeeding(Specie specie) {
		SpecieId specieId = specie.getId();

		List<Summary> summary = new ArrayList<Summary>();
		Player player = getPlayer(specieId);
		if (specie.isIntelligent() && player.getHandSize() > 0) {
			summary.add(
					new IntelligentFeedSummary(specieId,
							process(intelligentPlantEat(specieId, player.getHands().get(0))).getEvents(), 1));
		}

		plantEat(specieId)
				.ifPresent(foodEaten -> summary.add(new FeedSummary(specieId, process(foodEaten).getEvents())));

		return summary.stream();
	}

	public AttackSummary summarize(AttackViolations violations) {
		SpecieId source = violations.getSource();

		Player player = getPlayer(source);
		int handsSize = player.getHandSize();

		List<AttackOutcome> outcomes = new ArrayList<>();
		if (violations.isPossible(handsSize)) {
			List<String> must = violations.getMustPay();
			CardIdSequence seq = new CardIdSequence(player);
			outcomes.add(outcome(player, violations, must, seq));

			List<String> may = violations.getMayPay();
			int available = Math.min(handsSize - must.size(), may.size());
			for (int i = 0; i < available; ++i) {
				List<String> dis = new ArrayList<>(must);
				dis.add(may.get(i));
				outcomes.add(outcome(player, violations, dis, seq));
			}

		}
		return new AttackSummary(source, violations.getTarget(), violations.getViolations(), outcomes);
	}

	private AttackOutcome outcome(Player player ,AttackViolations violations, List<String> disabled, CardIdSequence seq) {

		AttackCommand command = new AttackCommand(violations,
				disabled.stream().collect(Collectors.toMap(Function.identity(), seq.reset())));
		Attacked attacked = command.resolve(violations, player);

		return new AttackOutcome(process(attacked).getEvents(), disabled.size(), disabled);
	}

	public AttackViolations attack(Specie source, Specie target) {
		return attack(source, target, species.forPlayer(target.getId().getPlayer()));
	}

	public AttackViolations attack(Specie source, Specie target, PlayerSpecies arround) {
		List<AttackViolation> violations = new ArrayList<>();

		sizeViolations(source, target).forEach(violations::add);
		climbingViolations(source, target).forEach(violations::add);
		burrowingViolations(source, target).forEach(violations::add);
		defensiveHerdingViolations(source, target).forEach(violations::add);
		hornsViolations(source, target).forEach(violations::add);
		quillsViolations(source, target).forEach(violations::add);
		symbiosisViolations(source, target, arround).forEach(violations::add);
		warningCallViolations(source, target, arround, true).forEach(violations::add);
		warningCallViolations(source, target, arround, false).forEach(violations::add);

		return new AttackViolations(source.getId(), target.getId(), violations);
	}

	private Stream<AttackViolation> warningCallViolations(Specie source, Specie target, PlayerSpecies arround,
			boolean left) {
		Optional<Specie> opt = (left ? arround.leftOf(target) : arround.rightOf(target))
				.filter(Specie::isWarningCall);

		if (opt.isPresent()) {
			String key = left ? "left_warning_call" : "right_warning_call";

			Specie other = opt.get();
			UsedTrait warningCall = other.usedTrait(Trait.WARNING_CALL);
			if (source.isAmbush()) {
				return Stream.of(new AttackViolation(key, warningCall,
						AttackViolationStatus.BYPASS, source.usedTrait(Trait.AMBUSH)));
			} else if (source.isIntelligent()) {
				return Stream.of(new AttackViolation(key, warningCall,
						AttackViolationStatus.MAY_PAY, source.usedTrait(Trait.INTELLIGENT)));
			} else {
				return Stream.of(new AttackViolation(key, warningCall,
						AttackViolationStatus.BLOCK, null));
			}
		}
		return Stream.empty();
	}

	private Stream<AttackViolation> symbiosisViolations(Specie source, Specie target, PlayerSpecies arround) {
		if (target.isSymbiosis()) {
			Optional<Specie> opt = arround.rightOf(target)
					.filter(r -> r.getSize() > target.getSize());
			if (opt.isPresent()) {
				UsedTrait symbiosis = target.usedTrait(Trait.SYMBIOSIS);
				if (source.isIntelligent()) {
					return Stream.of(new AttackViolation("symbiosis", symbiosis,
							AttackViolationStatus.MUST_PAY, source.usedTrait(Trait.INTELLIGENT)));
				} else {
					return Stream.of(new AttackViolation("symbiosis", symbiosis,
							AttackViolationStatus.BLOCK, null));
				}
			}
		}
		return Stream.empty();
	}

	private Stream<AttackViolation> hornsViolations(Specie source, Specie target) {
		if (target.isHorns()) {
			UsedTrait horns = target.usedTrait(Trait.HORNS);

			if (source.isIntelligent()) {
				return Stream.of(new AttackViolation("horns", horns,
						AttackViolationStatus.MAY_PAY, source.usedTrait(Trait.INTELLIGENT)));
			}

			return Stream.of(new AttackViolation("horns", horns,
					AttackViolationStatus.ACCEPT, null));
		}
		return Stream.empty();
	}

	private Stream<AttackViolation> quillsViolations(Specie source, Specie target) {
		Optional<UsedTrait> quilled = turnStatus.quilled(source.getId());
		if (quilled.isPresent()) {
			return quilled.map(t -> new AttackViolation("quilled", t, AttackViolationStatus.BLOCK, null)).stream();
		}

		if (target.isQuills()) {
			UsedTrait quills = target.usedTrait(Trait.QUILLS);

			if (source.isIntelligent()) {
				return Stream.of(new AttackViolation("quills", quills,
						AttackViolationStatus.MAY_PAY, source.usedTrait(Trait.INTELLIGENT)));
			}

			return Stream.of(new AttackViolation("quills", quills,
					AttackViolationStatus.ACCEPT, null));
		}
		return Stream.empty();
	}

	private Stream<AttackViolation> burrowingViolations(Specie source, Specie target) {
		if (target.isBurrowing() && target.isFed()) {
			UsedTrait burrowing = target.usedTrait(Trait.BURROWING);
			if (source.isIntelligent()) {
				return Stream.of(new AttackViolation("burrowing", burrowing,
						AttackViolationStatus.MUST_PAY, source.usedTrait(Trait.INTELLIGENT)));
			}
			return Stream.of(new AttackViolation("burrowing", burrowing,
					AttackViolationStatus.BLOCK, null));
		}
		return Stream.empty();
	}

	private Stream<AttackViolation> defensiveHerdingViolations(Specie source, Specie target) {
		if (target.isDefensiveHerding() && target.getPopulation() >= source.getPopulation()) {
			UsedTrait defensive = target.usedTrait(Trait.DEFENSIVE_HERDING);
			if (source.isIntelligent()) {
				return Stream.of(new AttackViolation("defensive_herding", defensive,
						AttackViolationStatus.MUST_PAY, source.usedTrait(Trait.INTELLIGENT)));
			}
			return Stream.of(new AttackViolation("defensive_herding", defensive,
					AttackViolationStatus.BLOCK, null));
		}
		return Stream.empty();
	}

	private Stream<AttackViolation> climbingViolations(Specie source, Specie target) {
		if (target.isClimbing()) {
			UsedTrait sourceClimbing = target.usedTrait(Trait.CLIMBING);
			if (source.isClimbing()) {
				return Stream.of(new AttackViolation("climbing", sourceClimbing,
						AttackViolationStatus.BYPASS, target.usedTrait(Trait.CLIMBING)));
			} else if (source.isIntelligent()) {
				return Stream.of(new AttackViolation("climbing", sourceClimbing,
						AttackViolationStatus.MUST_PAY, source.usedTrait(Trait.INTELLIGENT)));
			}
			return Stream.of(new AttackViolation("climbing", sourceClimbing,
					AttackViolationStatus.BLOCK, null));
		}
		return Stream.empty();
	}

	private Stream<AttackViolation> sizeViolations(Specie source, Specie target) {

		if (source.isVenom() && !turnStatus.hasAttacked(source.getId())) {
			return Stream
					.of(new AttackViolation("size", null, AttackViolationStatus.BYPASS, source.usedTrait(Trait.VENOM)));
		}

		int hardShellBonus = 3;
		if (source.getSize() <= target.getSize()) {
			if (source.isPackHunting()) {
				int strenght = source.getSize() + source.getPopulation();
				if (strenght > target.getSize()) {
					UsedTrait packHunting = source.usedTrait(Trait.PACK_HUNTING);
					if (target.isHardShell()) {
						UsedTrait hardShell = target.usedTrait(Trait.HARD_SHELL);
						if (strenght > target.getSize() + hardShellBonus) {
							return Stream.of(new AttackViolation("hard_shell", hardShell,
									AttackViolationStatus.BYPASS, packHunting));
						} else {
							if (source.isIntelligent()) {
								return Stream.of(new AttackViolation("hard_shell", hardShell,
										AttackViolationStatus.MUST_PAY, source.usedTrait(Trait.INTELLIGENT)),
										new AttackViolation("size", null, AttackViolationStatus.BYPASS, packHunting));
							} else {
								return Stream.of(new AttackViolation("hard_shell", hardShell,
										AttackViolationStatus.BLOCK, null));
							}

						}
					} else {
						return Stream.of(new AttackViolation("size", null, AttackViolationStatus.BYPASS, packHunting));
					}
				}
			} else {
				return Stream.of(new AttackViolation("size", null, AttackViolationStatus.BLOCK, null));
			}
		}

		if (target.isHardShell()) {
			UsedTrait hardShell = target.usedTrait(Trait.HARD_SHELL);
			if (source.getSize() <= target.getSize() + hardShellBonus) {
				if (source.isIntelligent()) {
					return Stream.of(new AttackViolation("hard_shell", hardShell,
							AttackViolationStatus.MUST_PAY, source.usedTrait(Trait.INTELLIGENT)));
				} else {
					return Stream.of(new AttackViolation("hard_shell", hardShell,
							AttackViolationStatus.BLOCK, null));
				}

			}
		}

		return Stream.empty();
	}

	private void attacked(EventConsumer<Event> consumer, Attacked attacked) {
		Specie target = species.forEvent(attacked);
		Specie attacker = species.byId(attacked.getAttacker());

		UsedTrait carnivorous = attacker.usedTrait(Trait.CARNIVOROUS);
		consumer.event(new PopulationReduced(target.getId(), target.getPopulation() - 1, carnivorous));

		SpecieId attackerId = attacker.getId();
		if (target.isHorns() && !attacked.traitDisabled(Trait.HORNS)) {
			PopulationReduced popReduced = new PopulationReduced(attackerId, attacker.getPopulation() - 1,
					target.usedTrait(Trait.HORNS));
			consumer.event(popReduced);
			attacker = attacker.changePopulation(popReduced);
		}

		if (target.isQuills() && !attacked.traitDisabled(Trait.QUILLS)) {
			consumer.event(new Quilled(attackerId, target.usedTrait(Trait.QUILLS)));
		}

		FoodSource source = FoodSource.ATTACK;
		int eaten = attacker.consumable(source, target.getSize());
		if (eaten > 0) {
			Set<UsedTrait> traits = new LinkedHashSet<>();
			traits.add(carnivorous);
			for (DisabledViolation d : attacked.getDisabled())
				traits.add(d.getUsed());

			FoodConsumption consumption = attacker.consumption(eaten);
			feedingUsedTraits(attacker, source, consumption, traits);
			consumer.event(new FoodEaten(attackerId, consumption, source, new ArrayList<>(traits), null));
		}
	}

	private void cooperation(EventConsumer<Event> consumer, FoodEaten eaten, Specie specie) {
		species.rightOf(specie).ifPresent(r -> {
			FoodSource source = eaten.getSource();
			int max = r.consumable(source, 1);
			if (source.isFoodPool())
				max = Math.min(max, pool.getFood());
			if (max > 0) {

				List<UsedTrait> traits = new ArrayList<>();
				traits.add(specie.usedTrait(Trait.COOPERATION));

				FoodConsumption consumption = r.consumption(max);
				feedingUsedTraits(r, source, consumption, traits);
				consumer.event(new FoodEaten(r.getId(), consumption, source, traits, null));
			}

		});
	}

	private void feedingUsedTraits(Specie r, FoodSource source, FoodConsumption consumption,
			Collection<UsedTrait> traits) {
		if (consumption.getConsumed() >= 2 && r.isForaging() && source.isPlantBased())
			traits.add(r.usedTrait(Trait.FORAGING));

		if (consumption.getFat() > 0)
			traits.add(r.usedTrait(Trait.FAT_TISSUE));
	}

	private void foodEaten(EventConsumer<Event> consumer, FoodEaten eaten) {
		Specie specie = species.forEvent(eaten);

		PlayArea me = this;
		if (specie.isPest() && specie.isFed()) {
			me = pest(consumer, specie);
		}

		if (specie.isCooperation()) {
			me.cooperation(consumer, eaten, specie);
		}

		if (eaten.isAttack()) {
			me.scavenger(consumer, eaten);
		}
	}

	private PlayArea pest(EventConsumer<Event> consumer, Specie specieEvent) {
		int food = pool.getFood();
		int delta = -3;
		int total = food + delta;
		if (total < 0) {
			total = 0;
			delta = -food;
		}
		DiscardPoolFood discard = new DiscardPoolFood(delta, total, specieEvent.usedTrait(Trait.PEST));
		consumer.event(discard);
		return accept(discard);
	}

	public Players getPlayers() {
		return players;
	}

	public List<CardDealed> handleDealCards() {
		Picker picker = deck.picker();
		return players.getPlayers().stream().map(p -> {
			int playerId = p.getId();
			int count = species.forPlayer(playerId).size() + 3;
			return pickForPlayer(picker, playerId, count);
		}).collect(Collectors.toList());
	}

	private CardDealed pickForPlayer(Picker picker, int playerId, int count) {
		List<Card> picked = picker.pick(count);
		boolean shuffled = picker.isShuffled();
		return new CardDealed(playerId, picked, shuffled, shuffled ? picker.getCards() : null);
	}

	/**
	 * Accept a command from a player
	 * 
	 * @param playerCommand
	 * @return the event to be processed
	 */
	public Event handleCommand(PlayerCommand playerCommand) {

		Command command = playerCommand.getCommand();
		Player player = getPlayer(playerCommand.getPlayer());

		if (command instanceof SpecieCommand) {
			SpecieCommand sc = (SpecieCommand) command;
			if (sc.getSpecie().getPlayer() != player.getId()) {
				throw new IllegalArgumentException("illegal player");
			}
		}

		if (command instanceof AttackCommand) {
			AttackCommand attack = (AttackCommand) command;
			attack.getViolations().values().forEach(player::checkCard);

			AttackViolations violations = attack(species.byId(attack.getSpecie()), species.byId(attack.getTarget()));
			return attack.resolve(violations, player);
		} else if (command instanceof IntelligentFeedCommand) {
			IntelligentFeedCommand feed = (IntelligentFeedCommand) command;
			return intelligentPlantEat(feed.getSpecie(), player.inHand(feed.getDiscarded()));
		} else if (command instanceof FeedCommand) {
			FeedCommand feed = (FeedCommand) command;
			return plantEat(feed.getSpecie()).orElseThrow();

		}

		return players.handleCommand(playerCommand, species);
	}

	public List<SpecieEvent> handleCleanUp() {
		return species.stream()
				.flatMap(s -> {
					int food = s.getFood();
					int population = s.getPopulation();
					SpecieId id = s.getId();

					List<SpecieEvent> out = new ArrayList<>();
					if (food < population)
						out.add(new PopulationReduced(id, food, null));

					if (food > 0)
						out.add(new FoodScored(id, food));

					return out.stream();
				})
				.collect(Collectors.toList());
	}

	public List<FatMoved> handleFats() {
		return species.stream().filter(s -> s.isFatTissue() && s.getFat() > 0)
				.map(s -> {
					int amount = Math.min(s.getPopulation(), s.getFat());
					return new FatMoved(s.getId(), amount);

				}).collect(Collectors.toList());
	}

	public List<TraitsRevealed> handleTraitsRevealed() {
		return species.stream().flatMap(s -> s.handleTraitsRevealed().stream()).collect(Collectors.toList());
	}

	public List<PopulationGrow> handleFertiles() {
		List<PopulationGrow> grow = Collections.emptyList();

		if (pool.getFood() > 0) {
			grow = species.stream().filter(s -> s.isFertile() && s.getSize() < 6)
					.map(s -> new PopulationGrow(s.getId(), s.getSize() + 1, s.usedTrait(Trait.FERTILE)))
					.collect(Collectors.toList());
		}

		return grow;
	}

	private Player getPlayer(SpecieId specie) {
		return getPlayer(specie.getPlayer());
	}

	public Player getPlayer(int player) {
		return players.getPlayers().get(player);
	}

	public List<FoodEaten> handleLongNecks() {
		return species.stream().filter(Specie::isLongNeck)
				.map(s -> {
					FoodSource source = FoodSource.PLANT;
					FoodConsumption consume = s.consumption(s.consumable(source, 1));

					List<UsedTrait> traits = new ArrayList<>();
					traits.add(s.usedTrait(Trait.LONGNECK));
					feedingUsedTraits(s, source, consume, traits);
					return new FoodEaten(s.getId(), consume, source, traits, null);

				}).collect(Collectors.toList());
	}

	public PoolRevealed handlePoolReveal() {
		return pool.handlePoolReveal();
	}

	@Override
	public PlayArea process(EventConsumer<Event> consumer, Event event) {
		PlayArea accept = accept(event);
		accept.react(consumer, event);
		return accept;
	}

	private void react(EventConsumer<Event> consumer, Event event) {
		if (event instanceof FoodEaten) {
			foodEaten(consumer, (FoodEaten) event);
		} else if (event instanceof Attacked) {
			attacked(consumer, (Attacked) event);
		} else if (event instanceof PopulationReduced) {
			reduced(consumer, (PopulationReduced) event);
		} else if (event instanceof SpecieExtincted) {
			extincted(consumer, (SpecieExtincted) event);
		} else if (event instanceof CardDealed)
			cardDealed(consumer, (CardDealed) event);
	}

	private void cardDealed(EventConsumer<Event> consumer, CardDealed dealed) {
		if (dealed.isShuffle()) {
			consumer.event(new LastTurnEvent(true));
		}
	}

	private void reduced(EventConsumer<Event> consumer, PopulationReduced reduced) {
		if (reduced.getTo() == 0) {
			Specie specie = species.forEvent(reduced);
			consumer.event(
					new SpecieExtincted(reduced.getSrc(), specie.accumulatedFood(), specie.getTraits()));
		}
	}

	private void extincted(EventConsumer<Event> consumer, SpecieExtincted extincted) {
		int player = extincted.getPlayer();
		if (species.forPlayer(player).size() == 0) {
			// recreate a specie when last specie is extincted
			consumer.event(addSpecie(player, null, SpeciePosition.RIGHT));
		}

		int draw = extincted.getTraits().size();
		if (draw > 0) {
			CardDealed pickForPlayer = pickForPlayer(deck.picker(), player, draw);
			consumer.event(pickForPlayer);

		}
	}

	private void scavenger(EventConsumer<Event> consumer, FoodEaten eaten) {
		FoodSource source = eaten.getSource().derive();

		species.stream().filter(Specie::isScavenger).forEach(s -> {
			int consumable = s.consumable(source, 1);
			if (consumable > 0) {

				FoodConsumption consumption = s.consumption(consumable);
				List<UsedTrait> traits = new ArrayList<>();
				traits.add(s.usedTrait(Trait.SCAVENGER));

				feedingUsedTraits(s, source, consumption, traits);

				consumer.event(
						new FoodEaten(s.getId(), consumption, source, traits, null));
			}
		});
	}

	public Specie getSpecie(SpecieId src) {
		return species.byId(src);
	}

	public SpecieAdded addSpecie(int player, CardId discarded, SpeciePosition position) {
		return new SpecieAdded(species.newId(player), position, discarded);
	}

	public FoodPool getPool() {
		return pool;
	}

	public TurnStatus getTurnStatus() {
		return turnStatus;
	}

}
