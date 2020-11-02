package fr.keyser.evolution.engine;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

import fr.keyser.evolution.event.CardDealed;
import fr.keyser.evolution.event.NextStepEvent;
import fr.keyser.evolution.event.PlayerPassedEvent;
import fr.keyser.evolution.event.PlayerStateChanged;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({ //
		@JsonSubTypes.Type(PlayerStateChanged.class),
		@JsonSubTypes.Type(PlayerPassedEvent.class),
		@JsonSubTypes.Type(NextStepEvent.class),
		@JsonSubTypes.Type(CardDealed.class),
		//
})
public interface Event {

}
