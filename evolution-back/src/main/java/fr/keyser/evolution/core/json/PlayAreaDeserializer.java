package fr.keyser.evolution.core.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import fr.keyser.evolution.core.Card;
import fr.keyser.evolution.core.Deck;
import fr.keyser.evolution.core.FoodPool;
import fr.keyser.evolution.core.PlayArea;
import fr.keyser.evolution.core.Players;
import fr.keyser.evolution.core.Species;
import fr.keyser.evolution.core.TurnStatus;

public class PlayAreaDeserializer extends StdDeserializer<PlayArea> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6989477813667141634L;

	public PlayAreaDeserializer() {
		super(PlayArea.class);
	}

	@Override
	public PlayArea deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		ObjectCodec codec = p.getCodec();
		JsonNode node = codec.readTree(p);

		Deck deck = codec.treeToValue(node.get("deck"), Deck.class);
		Card.useDeck(deck);

		Players players = codec.treeToValue(node.get("players"), Players.class);
		Species species = codec.treeToValue(node.get("species"), Species.class);
		FoodPool pool = codec.treeToValue(node.get("pool"), FoodPool.class);
		TurnStatus turnStatus = codec.treeToValue(node.get("turnStatus"), TurnStatus.class);

		return new PlayArea(players, deck, species, pool, turnStatus);
	}

}
