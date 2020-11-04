package fr.keyser.evolution.overview;

import java.util.List;

public interface GameOverviewRepository {

	public List<GameOverview> myGames(String userId);
}
