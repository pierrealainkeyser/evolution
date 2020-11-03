package fr.keyser.evolution.engine;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

import fr.keyser.evolution.event.Attacked;
import fr.keyser.evolution.event.CardAddedToPool;
import fr.keyser.evolution.event.CardDealed;
import fr.keyser.evolution.event.DiscardPoolFood;
import fr.keyser.evolution.event.FatMoved;
import fr.keyser.evolution.event.FoodEaten;
import fr.keyser.evolution.event.FoodScored;
import fr.keyser.evolution.event.LastTurnEvent;
import fr.keyser.evolution.event.NewTurnEvent;
import fr.keyser.evolution.event.NextPlayerEvent;
import fr.keyser.evolution.event.NextStepEvent;
import fr.keyser.evolution.event.PlayerPassedEvent;
import fr.keyser.evolution.event.PlayerStateChanged;
import fr.keyser.evolution.event.PoolRevealed;
import fr.keyser.evolution.event.PopulationGrow;
import fr.keyser.evolution.event.PopulationIncreased;
import fr.keyser.evolution.event.PopulationReduced;
import fr.keyser.evolution.event.Quilled;
import fr.keyser.evolution.event.SizeIncreased;
import fr.keyser.evolution.event.SpecieAdded;
import fr.keyser.evolution.event.SpecieExtincted;
import fr.keyser.evolution.event.TraitAdded;
import fr.keyser.evolution.event.TraitsRevealed;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({ //
		@JsonSubTypes.Type(PlayerStateChanged.class),
		@JsonSubTypes.Type(PlayerPassedEvent.class),
		@JsonSubTypes.Type(LastTurnEvent.class),
		@JsonSubTypes.Type(NextPlayerEvent.class),
		@JsonSubTypes.Type(NewTurnEvent.class),
		@JsonSubTypes.Type(NextStepEvent.class),
		@JsonSubTypes.Type(CardDealed.class),
		@JsonSubTypes.Type(Attacked.class),
		@JsonSubTypes.Type(FatMoved.class),
		@JsonSubTypes.Type(FoodEaten.class),
		@JsonSubTypes.Type(FoodScored.class),
		@JsonSubTypes.Type(PopulationGrow.class),
		@JsonSubTypes.Type(PopulationIncreased.class),
		@JsonSubTypes.Type(PopulationReduced.class),
		@JsonSubTypes.Type(Quilled.class),
		@JsonSubTypes.Type(SizeIncreased.class),
		@JsonSubTypes.Type(SpecieAdded.class),
		@JsonSubTypes.Type(SpecieExtincted.class),
		@JsonSubTypes.Type(TraitAdded.class),
		@JsonSubTypes.Type(TraitsRevealed.class),
		@JsonSubTypes.Type(CardAddedToPool.class),
		@JsonSubTypes.Type(DiscardPoolFood.class),
		@JsonSubTypes.Type(PoolRevealed.class),

		//
})
public interface Event {

}
