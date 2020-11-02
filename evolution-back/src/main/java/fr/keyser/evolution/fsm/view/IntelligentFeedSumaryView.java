package fr.keyser.evolution.fsm.view;

import java.util.List;

import fr.keyser.evolution.model.SpecieId;

public class IntelligentFeedSumaryView implements SummaryView {

	private final SpecieId specie;

	private final UsedTraitView trait;

	private final List<RenderedEvent> events;

	public IntelligentFeedSumaryView(SpecieId specie, UsedTraitView trait, List<RenderedEvent> events) {
		this.specie = specie;
		this.trait = trait;
		this.events = events;
	}

	@Override
	public String getType() {
		return "intelligent-feed";
	}

	public List<RenderedEvent> getEvents() {
		return events;
	}

	public SpecieId getSpecie() {
		return specie;
	}

	public UsedTraitView getTrait() {
		return trait;
	}

}
