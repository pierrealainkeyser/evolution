package fr.keyser.evolution.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import fr.keyser.evolution.command.AddCardToPoolCommand;
import fr.keyser.evolution.command.AddSpeciesCommand;
import fr.keyser.evolution.command.AddTraitCommand;
import fr.keyser.evolution.command.Command;
import fr.keyser.evolution.command.IncreasePopulationCommand;
import fr.keyser.evolution.command.IncreaseSizeCommand;
import fr.keyser.evolution.command.PlayerCommand;
import fr.keyser.evolution.command.SpecieCardCommand;
import fr.keyser.evolution.event.Attacked;
import fr.keyser.evolution.event.CardDealed;
import fr.keyser.evolution.event.DiscardedEvent;
import fr.keyser.evolution.event.PlayerEvent;
import fr.keyser.evolution.event.Scored;

public class Players {

	public static final Players players(int count) {
		return new Players(IntStream.range(0, count).mapToObj(Player::id).collect(Collectors.toList()));
	}

	private final List<Player> players;

	private Players(List<Player> players) {
		this.players = Collections.unmodifiableList(players);
	}

	public DiscardedEvent handleCommand(PlayerCommand playerCommand, Species species) {
		Player p = players.get(playerCommand.getPlayer());
		Command command = playerCommand.getCommand();
		if (command instanceof AddCardToPoolCommand)
			return p.handleAddToPool((AddCardToPoolCommand) command);
		else if (command instanceof AddSpeciesCommand) {
			return p.handleAddSpecie((AddSpeciesCommand) command, species.newId(p.getId()));
		} else if (command instanceof SpecieCardCommand) {
			SpecieCardCommand scommand = (SpecieCardCommand) command;
			Specie specie = species.byId(scommand.getSpecie());
			if (scommand instanceof AddTraitCommand)
				return p.handleAddTrait((AddTraitCommand) scommand, specie);
			else if (scommand instanceof IncreasePopulationCommand)
				return p.handleIncreasePopulation((IncreasePopulationCommand) scommand, specie);
			else if (scommand instanceof IncreaseSizeCommand)
				return p.handleIncreaseSize((IncreaseSizeCommand) scommand, specie);
		}

		return null;
	}

	public Players accept(PlayerEvent pe) {
		if (pe instanceof DiscardedEvent) {
			DiscardedEvent de = (DiscardedEvent) pe;
			return update(de.getPlayer(), p -> p.discard(de));
		} else if (pe instanceof Scored) {
			Scored s = (Scored) pe;
			return update(s.getPlayer(), p -> p.score(s));
		} else if (pe instanceof CardDealed) {
			CardDealed cd = (CardDealed) pe;
			return update(cd.getPlayer(), p -> p.addCards(cd));
		} else if (pe instanceof Attacked) {
			Attacked a = (Attacked) pe;
			return update(a.getPlayer(), p -> p.discard(a));
		}

		return this;
	}

	public int size() {
		return players.size();
	}

	private Players update(int index, UnaryOperator<Player> op) {
		List<Player> players = new ArrayList<>(this.players);
		players.set(index, op.apply(players.get(index)));
		return new Players(players);
	}

	public List<Player> getPlayers() {
		return players;
	}
}
