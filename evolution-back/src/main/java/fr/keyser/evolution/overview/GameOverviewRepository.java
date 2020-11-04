package fr.keyser.evolution.overview;

import java.util.List;

import fr.keyser.security.AuthenticatedPlayer;

public interface GameOverviewRepository {

	public List<GameOverview> myGames(AuthenticatedPlayer player);
}
