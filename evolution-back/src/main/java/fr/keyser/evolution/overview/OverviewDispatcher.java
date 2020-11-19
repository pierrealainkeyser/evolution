package fr.keyser.evolution.overview;

import java.util.List;

public interface OverviewDispatcher {

	public void dispatch(String type, List<GameOverview> overviews);

}
