package fr.keyser.evolution.fsm;

import fr.keyser.fsm.impl.AutomatEngine;

public interface GameResolver {

	public ResolvedRef findByUuid(String uuid);

	public AutomatEngine getEngine(GameRef ref);

	public void updateEngine(GameRef ref, AutomatEngine engine);

	public void addGame(ActiveGame active);

}
