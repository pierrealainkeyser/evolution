package fr.keyser.evolution.command;

import fr.keyser.evolution.model.SpecieId;

public abstract class FeedingPhaseCommand implements SpecieCommand {

	private final SpecieId specie;

	protected FeedingPhaseCommand(SpecieId specie) {
		this.specie = specie;
	}

	@Override
	public SpecieId getSpecie() {
		return specie;
	}

}
