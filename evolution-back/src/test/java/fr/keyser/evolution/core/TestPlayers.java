package fr.keyser.evolution.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import fr.keyser.evolution.command.AddCardToPoolCommand;
import fr.keyser.evolution.command.AddSpeciesCommand;
import fr.keyser.evolution.command.AddTraitCommand;
import fr.keyser.evolution.command.IncreasePopulationCommand;
import fr.keyser.evolution.command.IncreaseSizeCommand;
import fr.keyser.evolution.command.PlayerCommand;
import fr.keyser.evolution.engine.Event;
import fr.keyser.evolution.event.CardAddedToPool;
import fr.keyser.evolution.event.CardDealed;
import fr.keyser.evolution.event.PopulationIncreased;
import fr.keyser.evolution.event.SizeIncreased;
import fr.keyser.evolution.event.SpecieAdded;
import fr.keyser.evolution.event.TraitAdded;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.SpeciePosition;
import fr.keyser.evolution.model.Trait;

public class TestPlayers {

	@Test
	void toPool() {
		DeckBuilder builder = new DeckBuilder();
		Players players = Players.players(1);
		Species species = Species.INITIAL;

		Card foraging = builder.card(Trait.FORAGING, 5);
		players = players.accept(new CardDealed(0, Arrays.asList(foraging), false, null));

		Event event = players.handleCommand(new PlayerCommand(0, new AddCardToPoolCommand(foraging.getId())),
				species);

		assertThat(event).isInstanceOf(CardAddedToPool.class);
	}

	@Test
	void species() {
		DeckBuilder builder = new DeckBuilder();
		Players players = Players.players(1);
		Species species = Species.INITIAL;

		Card foraging = builder.card(Trait.FORAGING, 5);
		Card burrowing = builder.card(Trait.BURROWING, 5);
		players = players.accept(new CardDealed(0, Arrays.asList(foraging, burrowing), false, null));

		Event event = players.handleCommand(
				new PlayerCommand(0, new AddSpeciesCommand(foraging.getId(), SpeciePosition.RIGHT)),
				species);

		assertThat(event).isInstanceOfSatisfying(SpecieAdded.class,
				a -> {
					assertThat(a.getSrc()).isNotNull();
					assertThat(a.getPlayer()).isEqualTo(0);
					assertThat(a.getDiscarded()).isEqualTo(foraging.getId());
				});
		SpecieAdded added = (SpecieAdded) event;
		species = species.accept(added);
		SpecieId src = added.getSrc();

		event = players.handleCommand(
				new PlayerCommand(0, new IncreasePopulationCommand(burrowing.getId(), src)),
				species);
		assertThat(event).isInstanceOfSatisfying(PopulationIncreased.class,
				p -> {
					assertThat(p.getTo()).isEqualTo(2);
				});

		event = players.handleCommand(
				new PlayerCommand(0, new IncreaseSizeCommand(burrowing.getId(), src)),
				species);
		assertThat(event).isInstanceOfSatisfying(SizeIncreased.class, p -> {
			assertThat(p.getTo()).isEqualTo(2);
		});

		event = players.handleCommand(
				new PlayerCommand(0, new AddTraitCommand(burrowing.getId(), src, 0)),
				species);
		assertThat(event).isInstanceOfSatisfying(TraitAdded.class, a -> {
			assertThat(a.getCard()).isSameAs(burrowing);
		});

	}

}
