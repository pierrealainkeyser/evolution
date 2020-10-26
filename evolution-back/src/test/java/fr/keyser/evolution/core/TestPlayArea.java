package fr.keyser.evolution.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.keyser.evolution.command.AttackCommand;
import fr.keyser.evolution.command.FeedCommand;
import fr.keyser.evolution.command.IntelligentFeedCommand;
import fr.keyser.evolution.command.PlayerCommand;
import fr.keyser.evolution.engine.Event;
import fr.keyser.evolution.engine.Events;
import fr.keyser.evolution.event.Attacked;
import fr.keyser.evolution.event.CardAddedToPool;
import fr.keyser.evolution.event.CardDealed;
import fr.keyser.evolution.event.FoodEaten;
import fr.keyser.evolution.event.PopulationIncreased;
import fr.keyser.evolution.event.SizeIncreased;
import fr.keyser.evolution.event.SpecieAdded;
import fr.keyser.evolution.event.TraitAdded;
import fr.keyser.evolution.model.AttackViolations;
import fr.keyser.evolution.model.Card;
import fr.keyser.evolution.model.FoodSource;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.SpeciePosition;
import fr.keyser.evolution.model.Trait;
import fr.keyser.evolution.summary.AttackSummary;
import fr.keyser.evolution.summary.FeedSummary;
import fr.keyser.evolution.summary.IntelligentFeedSummary;

public class TestPlayArea {

	@Test
	void nominal() {
		DeckBuilder builder = new DeckBuilder();
		PlayArea area = PlayArea.with(Players.players(3), Deck.INITIAL);

		area = run(area, area.addSpecie(0, null, SpeciePosition.LEFT));
		area = run(area, area.addSpecie(0, null, SpeciePosition.RIGHT));
		area = run(area, area.addSpecie(1, null, SpeciePosition.LEFT));

		area = run(area, new TraitAdded(new SpecieId(0, 0), builder.card(Trait.LONGNECK), 0, null));
		area = run(area, new TraitAdded(new SpecieId(0, 0), builder.card(Trait.COOPERATION), 1, null));

		area = run(area, new TraitAdded(new SpecieId(1, 0), builder.card(Trait.FORAGING), 0, null));
		area = run(area, new PopulationIncreased(new SpecieId(1, 0), 3, null));

		List<FoodEaten> longNecks = area.handleLongNecks();
		Assertions.assertThat(longNecks).hasSize(1);

		area = run(area, longNecks, true);

		area = run(area, area.handleCleanUp(), true);

		System.out.println(area.getPlayers().getPlayers());
	}

	@Test
	void pest() {
		DeckBuilder builder = new DeckBuilder();

		PlayArea area = PlayArea.with(Players.players(2), builder.deck());

		SpecieAdded s1 = area.addSpecie(0, null, SpeciePosition.LEFT);
		area = run(area, s1);

		area = run(area, new TraitAdded(s1.getSrc(), builder.card(Trait.PEST), 0, null));
		area = run(area, new CardAddedToPool(0, builder.card(Trait.FORAGING, 5)));
		area = run(area, area.handlePoolReveal());
		assertThat(area.getPool().getFood()).isEqualTo(5);

		Event event = area.handleCommand(new PlayerCommand(0, new FeedCommand(s1.getSrc())));
		area = run(area, event, true);
		assertThat(area.getPool().getFood()).isEqualTo(1);
	}

	@Test
	void summary() {
		DeckBuilder builder = new DeckBuilder();
		PlayArea area = PlayArea.with(Players.players(3), builder.deck());

		SpecieAdded s1 = area.addSpecie(0, null, SpeciePosition.LEFT);
		area = run(area, s1);
		SpecieAdded s2 = area.addSpecie(1, null, SpeciePosition.LEFT);
		area = run(area, s2);
		SpecieAdded s3 = area.addSpecie(2, null, SpeciePosition.LEFT);
		area = run(area, s3);

		area = run(area, new CardDealed(2, Arrays.asList(builder.card(Trait.FAT_TISSUE)), false, null));

		area = run(area, new CardAddedToPool(0, builder.card(Trait.FORAGING, 5)));
		area = run(area, area.handlePoolReveal());
		assertThat(area.getPool().getFood()).isEqualTo(5);
		area = run(area, new CardAddedToPool(0, builder.card(Trait.AMBUSH, 1)));
		area = run(area, area.handlePoolReveal());
		assertThat(area.getPool().getFood()).isEqualTo(6);

		area = run(area, new TraitAdded(s1.getSrc(), builder.card(Trait.FORAGING), 0, null));
		area = run(area, new PopulationIncreased(s1.getSrc(), 3, null));

		area = run(area, new TraitAdded(s2.getSrc(), builder.card(Trait.CARNIVOROUS), 0, null));
		area = run(area, new SizeIncreased(s2.getSrc(), 2, null));

		area = run(area, new TraitAdded(s3.getSrc(), builder.card(Trait.INTELLIGENT), 0, null));
		area = run(area, new PopulationIncreased(s3.getSrc(), 3, null));

		// check the plain feeding
		assertThat(area.actionsForPlayer(0))
				.hasSize(1)
				.anySatisfy(s -> {
					assertThat(s).isInstanceOfSatisfying(FeedSummary.class, f -> {
						assertThat(f.getSpecie()).isEqualTo(s1.getSrc());
						assertThat(f.getEvents())
								.hasSize(1)
								.anySatisfy(e -> {
									assertThat(e).isInstanceOfSatisfying(FoodEaten.class, eaten -> {
										assertThat(eaten.getConsumed()).isEqualTo(2);
										assertThat(eaten.getSource()).isEqualTo(FoodSource.POOL);
									});
								});
					});
				});

		// check the carnivorous
		assertThat(area.actionsForPlayer(1))
				.hasSize(2)
				.anySatisfy(s -> {
					assertThat(s).isInstanceOfSatisfying(AttackSummary.class, a -> {
						assertThat(a.getSpecie()).isEqualTo(s2.getSrc());
						assertThat(a.getTarget()).isEqualTo(s1.getSrc());
					});
				})
				.anySatisfy(s -> {
					assertThat(s).isInstanceOfSatisfying(AttackSummary.class, a -> {
						assertThat(a.getSpecie()).isEqualTo(s2.getSrc());
						assertThat(a.getTarget()).isEqualTo(s3.getSrc());
					});
				});

		// check the intelligent
		assertThat(area.actionsForPlayer(2))
				.hasSize(2)
				.anySatisfy(s -> {
					assertThat(s).isInstanceOfSatisfying(FeedSummary.class, f -> {
						assertThat(f.getSpecie()).isEqualTo(s3.getSrc());
						assertThat(f.getEvents()).hasSize(1);
					});
				})
				.anySatisfy(s -> {
					assertThat(s).isInstanceOfSatisfying(IntelligentFeedSummary.class, f -> {
						assertThat(f.getSpecie()).isEqualTo(s3.getSrc());
						assertThat(f.getEvents())
								.hasSize(1)
								.anySatisfy(e -> {
									assertThat(e).isInstanceOfSatisfying(FoodEaten.class, eaten -> {
										assertThat(eaten.getConsumed()).isEqualTo(2);
										assertThat(eaten.getSource()).isEqualTo(FoodSource.PLANT);
									});
								});
					});
				});

	}

	@Test
	void summaryIntelligentForaging() {
		DeckBuilder builder = new DeckBuilder();
		PlayArea area = PlayArea.with(Players.players(2), builder.deck());

		SpecieAdded s1 = area.addSpecie(0, null, SpeciePosition.LEFT);
		area = run(area, s1);
		SpecieAdded s2 = area.addSpecie(1, null, SpeciePosition.LEFT);
		area = run(area, s2);

		Card card = builder.card(Trait.FAT_TISSUE);
		area = run(area, new CardDealed(0, Arrays.asList(card, builder.card(Trait.FAT_TISSUE)), false, null));

		area = run(area, new TraitAdded(s1.getSrc(), builder.card(Trait.INTELLIGENT), 0, null));
		area = run(area, new TraitAdded(s1.getSrc(), builder.card(Trait.FORAGING), 1, null));
		area = run(area, new PopulationIncreased(s1.getSrc(), 3, null));

		area = run(area, new TraitAdded(s2.getSrc(), builder.card(Trait.INTELLIGENT), 0, null));
		area = run(area, new PopulationIncreased(s2.getSrc(), 3, null));

		// check the intelligent foraging
		assertThat(area.actionsForPlayer(0))
				.hasSize(1)
				.anySatisfy(s -> {
					assertThat(s).isInstanceOfSatisfying(IntelligentFeedSummary.class, f -> {
						assertThat(f.getSpecie()).isEqualTo(s1.getSrc());
						assertThat(f.getCost()).isEqualTo(1);
						assertThat(f.getEvents())
								.hasSize(1)
								.anySatisfy(e -> {
									assertThat(e).isInstanceOfSatisfying(FoodEaten.class, eaten -> {
										assertThat(eaten.getConsumed()).isEqualTo(3);
										assertThat(eaten.getSource()).isEqualTo(FoodSource.PLANT);

									});
								});
					});
				});

		// check the intelligent without cards
		assertThat(area.actionsForPlayer(1)).isEmpty();

		Event event = area.handleCommand(new PlayerCommand(0, new IntelligentFeedCommand(s1.getSrc(), card.getId())));
		assertThat(event).isInstanceOfSatisfying(FoodEaten.class, eaten -> {
			assertThat(eaten.getConsumed()).isEqualTo(3);
			assertThat(eaten.getSource()).isEqualTo(FoodSource.PLANT);
		});
		area = run(area, event);

		// no more actions
		assertThat(area.actionsForPlayer(0)).isEmpty();

	}

	@Test
	void attack() {

		DeckBuilder builder = new DeckBuilder();
		builder.create(Trait.AMBUSH);
		builder.create(Trait.WARNING_CALL);
		builder.create(Trait.BURROWING);
		builder.create(Trait.SYMBIOSIS);

		PlayArea area = PlayArea.with(Players.players(2), builder.deck());

		SpecieAdded s1 = area.addSpecie(0, null, SpeciePosition.LEFT);
		area = run(area, s1);
		SpecieAdded s2 = area.addSpecie(1, null, SpeciePosition.LEFT);
		area = run(area, s2);
		SpecieAdded s3 = area.addSpecie(0, null, SpeciePosition.RIGHT);
		area = run(area, s3);
		SpecieAdded s4 = area.addSpecie(0, null, SpeciePosition.RIGHT);
		area = run(area, s4);

		area = run(area, new CardDealed(0,
				Arrays.asList(builder.card(Trait.DEFENSIVE_HERDING), builder.card(Trait.FAT_TISSUE)), false, null));

		SpecieId attackerId = s1.getSrc();
		SpecieId defenderId = s2.getSrc();

		area = run(area, new PopulationIncreased(attackerId, 3, null));
		area = run(area, new TraitAdded(attackerId, builder.card(Trait.CARNIVOROUS), 0, null));
		area = run(area, new TraitAdded(attackerId, builder.card(Trait.PACK_HUNTING), 1, null));
		area = run(area, new TraitAdded(attackerId, builder.card(Trait.INTELLIGENT), 2, null));

		area = run(area, new TraitAdded(s3.getSrc(), builder.card(Trait.SCAVENGER), 0, null));
		area = run(area, new TraitAdded(s3.getSrc(), builder.card(Trait.COOPERATION), 1, null));

		area = run(area, new SizeIncreased(defenderId, 3, null));
		area = run(area, new TraitAdded(defenderId, builder.card(Trait.HORNS), 0, null));
		area = run(area, new TraitAdded(defenderId, builder.card(Trait.HARD_SHELL), 1, null));

		AttackViolations violations = area.attack(area.getSpecie(attackerId), area.getSpecie(defenderId));
		System.out.println("--");
		violations.getViolations().forEach(System.out::println);
		System.out.println("--");

		AttackSummary summary = area.summarize(violations);
		assertThat(summary.isPossible()).isTrue();
		System.out.println(summary.getSpecie() + "->" + summary.getTarget());
		summary.getViolations().forEach(System.out::println);
		assertThat(summary.getOutcomes()).hasSize(2);
		summary.getOutcomes().forEach(o -> System.out.println(" ->" + o));
		System.out.println("--");

		Player p = area.getPlayer(0);
		assertThat(violations.isPossible(p.getHandSize())).isTrue();

		run(area,
				new AttackCommand(violations, Map.of("hard_shell", p.getHands().get(0).getId())).resolve(violations),true);

		System.out.println("--");

		Event attacked = area.handleCommand(new PlayerCommand(0, new AttackCommand(violations,
				Map.of("hard_shell", p.getHands().get(0).getId(), "horns", p.getHands().get(1).getId()))));
		assertThat(attacked).isInstanceOf(Attacked.class);

		run(area, attacked, true);
	}

	@Test
	void attackViolations() {
		DeckBuilder builder = new DeckBuilder();

		PlayArea area = PlayArea.with(Players.players(2), builder.deck());

		SpecieAdded s1 = area.addSpecie(0, null, SpeciePosition.LEFT);
		area = run(area, s1);
		SpecieAdded s2 = area.addSpecie(1, null, SpeciePosition.RIGHT);
		area = run(area, s2);
		SpecieAdded s3 = area.addSpecie(1, null, SpeciePosition.RIGHT);
		area = run(area, s3);
		SpecieAdded s4 = area.addSpecie(1, null, SpeciePosition.RIGHT);
		area = run(area, s4);

		SpecieId attackerId = s1.getSrc();
		area = run(area, new SizeIncreased(attackerId, 3, null));
		SpecieId defender = s2.getSrc();
		area = run(area, new PopulationIncreased(defender, 3, null));

		area = run(area, new TraitAdded(attackerId, builder.card(Trait.CARNIVOROUS), 0, null));
		area = run(area, new TraitAdded(attackerId, builder.card(Trait.CLIMBING), 1, null));
		area = run(area, new TraitAdded(attackerId, builder.card(Trait.AMBUSH), 2, null));

		area = run(area, new TraitAdded(defender, builder.card(Trait.CLIMBING), 0, null));
		area = run(area, new TraitAdded(s3.getSrc(), builder.card(Trait.WARNING_CALL), 0, null));
		area = run(area, new TraitAdded(s3.getSrc(), builder.card(Trait.SYMBIOSIS), 1, null));
		area = run(area, new SizeIncreased(s4.getSrc(), 2, null));

		AttackSummary summary = area.summarize(area.attack(area.getSpecie(attackerId), area.getSpecie(defender)));
		assertThat(summary.isPossible()).isTrue();
		assertThat(summary.getOutcomes()).hasSize(1);

		summary = area.summarize(area.attack(area.getSpecie(attackerId), area.getSpecie(s3.getSrc())));
		assertThat(summary.isPossible()).isFalse();

		// no cards in hand
		area = run(area, new TraitAdded(attackerId, builder.card(Trait.INTELLIGENT), 2, null));
		summary = area.summarize(area.attack(area.getSpecie(attackerId), area.getSpecie(s3.getSrc())));
		assertThat(summary.isPossible()).isFalse();

		// symbiosis dont work anynmore
		area = run(area, new SizeIncreased(s3.getSrc(), 2, null));
		summary = area.summarize(area.attack(area.getSpecie(attackerId), area.getSpecie(s3.getSrc())));
		assertThat(summary.isPossible()).isTrue();

		// remove ambush
		area = run(area, new TraitAdded(attackerId, builder.card(Trait.FAT_TISSUE), 2, null));
		summary = area.summarize(area.attack(area.getSpecie(attackerId), area.getSpecie(defender)));
		assertThat(summary.isPossible()).isFalse();
	}

	@Test
	void ambush() {
		DeckBuilder builder = new DeckBuilder();

		PlayArea area = PlayArea.with(Players.players(2), builder.deck());

		SpecieAdded s1 = area.addSpecie(0, null, SpeciePosition.LEFT);
		area = run(area, s1);
		SpecieAdded s2 = area.addSpecie(1, null, SpeciePosition.RIGHT);
		area = run(area, s2);
		SpecieAdded s3 = area.addSpecie(1, null, SpeciePosition.RIGHT);
		area = run(area, s3);
		SpecieAdded s4 = area.addSpecie(1, null, SpeciePosition.RIGHT);
		area = run(area, s4);

		SpecieId attackerId = s1.getSrc();
		area = run(area, new SizeIncreased(attackerId, 3, null));
		area = run(area, new CardDealed(0,
				Arrays.asList(builder.card(Trait.DEFENSIVE_HERDING), builder.card(Trait.FAT_TISSUE)), false, null));

		SpecieId defender = s3.getSrc();
		area = run(area, new PopulationIncreased(defender, 3, null));

		area = run(area, new TraitAdded(attackerId, builder.card(Trait.CARNIVOROUS), 0, null));
		area = run(area, new TraitAdded(attackerId, builder.card(Trait.AMBUSH), 1, null));

		area = run(area, new TraitAdded(s2.getSrc(), builder.card(Trait.WARNING_CALL), 0, null));
		area = run(area, new TraitAdded(s4.getSrc(), builder.card(Trait.WARNING_CALL), 0, null));

		AttackSummary summary = area.summarize(area.attack(area.getSpecie(attackerId), area.getSpecie(defender)));
		assertThat(summary.isPossible()).isTrue();

		area = run(area, new TraitAdded(attackerId, builder.card(Trait.INTELLIGENT), 1, null));
		summary = area.summarize(area.attack(area.getSpecie(attackerId), area.getSpecie(defender)));
		assertThat(summary.isPossible()).isTrue();

		area = run(area, new TraitAdded(attackerId, builder.card(Trait.FAT_TISSUE), 1, null));
		summary = area.summarize(area.attack(area.getSpecie(attackerId), area.getSpecie(defender)));
		assertThat(summary.isPossible()).isFalse();
	}

	@Test
	void climbing() {
		DeckBuilder builder = new DeckBuilder();

		PlayArea area = PlayArea.with(Players.players(2), builder.deck());

		SpecieAdded s1 = area.addSpecie(0, null, SpeciePosition.LEFT);
		area = run(area, s1);
		SpecieAdded s2 = area.addSpecie(1, null, SpeciePosition.RIGHT);
		area = run(area, s2);

		SpecieId attackerId = s1.getSrc();
		area = run(area, new SizeIncreased(attackerId, 3, null));
		area = run(area, new CardDealed(0,
				Arrays.asList(builder.card(Trait.DEFENSIVE_HERDING)), false, null));

		SpecieId defender = s2.getSrc();
		area = run(area, new PopulationIncreased(defender, 3, null));

		area = run(area, new TraitAdded(attackerId, builder.card(Trait.CARNIVOROUS), 0, null));
		area = run(area, new TraitAdded(attackerId, builder.card(Trait.CLIMBING), 1, null));

		area = run(area, new TraitAdded(s2.getSrc(), builder.card(Trait.CLIMBING), 0, null));

		AttackSummary summary = area.summarize(area.attack(area.getSpecie(attackerId), area.getSpecie(defender)));
		assertThat(summary.isPossible()).isTrue();

		// replace climbing with intelligent (still possible)
		area = run(area, new TraitAdded(attackerId, builder.card(Trait.INTELLIGENT), 1, null));
		summary = area.summarize(area.attack(area.getSpecie(attackerId), area.getSpecie(defender)));
		assertThat(summary.isPossible()).isTrue();

		// replace intelligent with fat (not possible anymore)
		area = run(area, new TraitAdded(attackerId, builder.card(Trait.FAT_TISSUE), 1, null));
		summary = area.summarize(area.attack(area.getSpecie(attackerId), area.getSpecie(defender)));
		assertThat(summary.isPossible()).isFalse();
	}

	@Test
	void venom() {
		DeckBuilder builder = new DeckBuilder();

		PlayArea area = PlayArea.with(Players.players(2), builder.deck());

		SpecieAdded s1 = area.addSpecie(0, null, SpeciePosition.LEFT);
		area = run(area, s1);
		SpecieAdded s2 = area.addSpecie(1, null, SpeciePosition.RIGHT);
		area = run(area, s2);

		SpecieId attackerId = s1.getSrc();
		SpecieId defender = s2.getSrc();
		area = run(area, new SizeIncreased(defender, 3, null));
		area = run(area, new PopulationIncreased(defender, 2, null));

		area = run(area, new TraitAdded(attackerId, builder.card(Trait.CARNIVOROUS), 0, null));
		area = run(area, new TraitAdded(attackerId, builder.card(Trait.VENOM), 1, null));

		AttackSummary summary = area.summarize(area.attack(area.getSpecie(attackerId), area.getSpecie(defender)));
		assertThat(summary.isPossible()).isTrue();

		area = run(area, area.handleCommand(
				new PlayerCommand(0, new AttackCommand(attackerId, defender, Collections.emptyMap()))), true);

		summary = area.summarize(area.attack(area.getSpecie(attackerId), area.getSpecie(defender)));
		assertThat(summary.isPossible()).isFalse();

		// staart a new step
		TurnStatus ts = area.getTurnStatus();
		area = run(area, ts.nextStep(TurnStep.FEEDING));

		// the attack is possible
		summary = area.summarize(area.attack(area.getSpecie(attackerId), area.getSpecie(defender)));
		assertThat(summary.isPossible()).isTrue();
	}
	
	@Test
	void quills() {
		DeckBuilder builder = new DeckBuilder();

		PlayArea area = PlayArea.with(Players.players(2), builder.deck());

		SpecieAdded s1 = area.addSpecie(0, null, SpeciePosition.LEFT);
		area = run(area, s1);
		SpecieAdded s2 = area.addSpecie(1, null, SpeciePosition.RIGHT);
		area = run(area, s2);

		SpecieId attackerId = s1.getSrc();
		SpecieId defender = s2.getSrc();
		area = run(area, new SizeIncreased(attackerId, 2, null));
		area = run(area, new PopulationIncreased(defender, 2, null));
		area = run(area, new TraitAdded(attackerId, builder.card(Trait.CARNIVOROUS), 0, null));
		area = run(area, new TraitAdded(defender, builder.card(Trait.QUILLS), 0, null));

		AttackSummary summary = area.summarize(area.attack(area.getSpecie(attackerId), area.getSpecie(defender)));
		assertThat(summary.isPossible()).isTrue();

		area = run(area, area.handleCommand(
				new PlayerCommand(0, new AttackCommand(attackerId, defender, Collections.emptyMap()))), true);

		// the quills prevents the attack
		summary = area.summarize(area.attack(area.getSpecie(attackerId), area.getSpecie(defender)));
		assertThat(summary.isPossible()).isFalse();

		// staart a new step
		TurnStatus ts = area.getTurnStatus();
		area = run(area, ts.nextStep(TurnStep.FEEDING));

		// the attack is possible
		summary = area.summarize(area.attack(area.getSpecie(attackerId), area.getSpecie(defender)));
		assertThat(summary.isPossible()).isTrue();
	}

	private static PlayArea run(PlayArea area, Event evt) {
		return run(area, evt, false);
	}

	private static PlayArea run(PlayArea area, Event evt, boolean log) {
		return run(area, Arrays.asList(evt), log);
	}

	private static PlayArea run(PlayArea area, List<? extends Event> evts, boolean log) {
		Events<Event, PlayArea> events = area.process(evts);
		if (log)
			events.getEvents().forEach(System.out::println);
		return events.getOutput();
	}
}
