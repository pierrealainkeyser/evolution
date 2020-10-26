package fr.keyser.evolution.command;

import fr.keyser.evolution.model.SpecieId;

public interface SpecieCommand extends Command {

	public SpecieId getSpecie();
}
