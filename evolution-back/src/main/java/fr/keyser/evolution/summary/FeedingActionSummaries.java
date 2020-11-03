package fr.keyser.evolution.summary;

import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;

public class FeedingActionSummaries {

	private final List<FeedingActionSummary> actions;

	@JsonCreator
	public FeedingActionSummaries(@JsonUnwrapped List<FeedingActionSummary> actions) {
		this.actions = actions;
	}

	public Stream<FeedingActionSummary> stream() {
		return actions.stream();
	}

	@JsonValue
	public List<FeedingActionSummary> getActions() {
		return actions;
	}
}
