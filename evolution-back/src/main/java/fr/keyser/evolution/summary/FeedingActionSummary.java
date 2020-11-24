package fr.keyser.evolution.summary;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

import fr.keyser.evolution.model.SpecieId;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({ //
		@JsonSubTypes.Type(AttackSummary.class),
		@JsonSubTypes.Type(FeedSummary.class),
		@JsonSubTypes.Type(IntelligentFeedSummary.class),
})
public interface FeedingActionSummary {

	SpecieId getSpecie();

	public boolean isOptional();

	@JsonIgnore
	public default boolean isPossible() {
		return true;
	}
}
