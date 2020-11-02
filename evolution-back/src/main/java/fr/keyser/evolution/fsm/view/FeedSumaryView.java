package fr.keyser.evolution.fsm.view;

import java.util.List;

import fr.keyser.evolution.model.SpecieId;

public class FeedSumaryView implements SummaryView {

	private final SpecieId specie;

	private final List<RenderedEvent> events;

	public FeedSumaryView(SpecieId specie, List<RenderedEvent> events) {
		this.specie = specie;
		this.events = events;
	}

	@Override
	public String getType() {
		return "feed";
	}

	public SpecieId getSpecie() {
		return specie;
	}

	public List<RenderedEvent> getEvents() {
		return events;
	}

}
