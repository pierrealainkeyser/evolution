package fr.keyser.evolution.overview;

import java.util.List;

import fr.keyser.evolution.fsm.GameRef;
import fr.keyser.security.AuthenticatedPlayer;

public interface GameOverviewRepository {

	public List<GameOverview> overview(GameRef ref);

	public List<GameOverview> myGames(AuthenticatedPlayer player);
}
