package fr.keyser.evolution.fsm;

public interface GameLocker {

	public void withinLock(GameRef game, Runnable runnable);
}
