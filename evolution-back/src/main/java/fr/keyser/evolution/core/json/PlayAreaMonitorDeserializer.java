package fr.keyser.evolution.core.json;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import fr.keyser.evolution.core.PlayArea;
import fr.keyser.evolution.engine.Event;
import fr.keyser.evolution.fsm.PlayAreaMonitor;

public class PlayAreaMonitorDeserializer extends StdDeserializer<PlayAreaMonitor> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6989477813667141634L;

	public PlayAreaMonitorDeserializer() {
		super(PlayAreaMonitor.class);
	}

	@Override
	public PlayAreaMonitor deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		ObjectCodec codec = p.getCodec();
		JsonNode node = codec.readTree(p);

		PlayArea playArea = codec.treeToValue(node.get("area"), PlayArea.class);

		TypeReference<List<Event>> eventsType = new TypeReference<List<Event>>() {
		};

		List<Event> currents = codec.readValue(codec.treeAsTokens(node.get("currents")), eventsType);
		List<Event> history = codec.readValue(codec.treeAsTokens(node.get("history")), eventsType);

		int draw = node.get("draw").asInt();

		return new PlayAreaMonitor(playArea, currents, history, draw);

	}

}
