package fr.keyser.evolution.summary;

import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FeedingActionSummaries {

	private final List<FeedingActionSummary> actions;

	@JsonCreator
	public FeedingActionSummaries(@JsonProperty("actions") List<FeedingActionSummary> actions) {
		this.actions = actions;
	}
	
	@JsonIgnore
	public boolean isPass() {
		return  stream().allMatch(FeedingActionSummary::isOptional);
	}

	@JsonIgnore
	public boolean isImpossible() {
		return actions.isEmpty() || actions.stream().allMatch(f -> !f.isPossible());
	}

	public Stream<FeedingActionSummary> stream() {
		return actions.stream();
	}

	public List<FeedingActionSummary> getActions() {
		return actions;
	}
}
