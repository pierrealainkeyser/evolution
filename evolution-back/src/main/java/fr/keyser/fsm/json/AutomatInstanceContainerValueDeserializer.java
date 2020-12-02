package fr.keyser.fsm.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import fr.keyser.fsm.impl.AutomatInstanceContainerValue;
import fr.keyser.fsm.impl.AutomatInstanceValue;

public class AutomatInstanceContainerValueDeserializer extends StdDeserializer<AutomatInstanceContainerValue> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6989477813667141634L;

	private final JsonDataMapAdapter adapter;

	public AutomatInstanceContainerValueDeserializer(JsonDataMapAdapter adapter) {
		super(AutomatInstanceContainerValue.class);
		this.adapter = adapter;
	}

	@Override
	public AutomatInstanceContainerValue deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		ObjectCodec codec = p.getCodec();
		JsonNode node = codec.readTree(p);
		
		Map<String, Object> data = adapter.deserialize(node.get("data"), codec);

		List<AutomatInstanceValue> all = new ArrayList<>();

		JsonNode allNode = node.get("all");
		for (JsonNode sub : allNode)
			all.add(codec.treeToValue(sub, AutomatInstanceValue.class));
		
		return AutomatInstanceContainerValue.create(all, data);
	}

}
