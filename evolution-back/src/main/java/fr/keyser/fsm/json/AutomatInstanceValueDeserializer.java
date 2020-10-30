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

import fr.keyser.fsm.InstanceId;
import fr.keyser.fsm.State;
import fr.keyser.fsm.impl.AutomatInstanceValue;

public class AutomatInstanceValueDeserializer extends StdDeserializer<AutomatInstanceValue> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6989477813667141634L;

	private final JsonDataMapAdapter adapter;

	public AutomatInstanceValueDeserializer(JsonDataMapAdapter adapter) {
		super(AutomatInstanceValue.class);
		this.adapter = adapter;
	}

	@Override
	public AutomatInstanceValue deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		ObjectCodec codec = p.getCodec();
		JsonNode node = codec.readTree(p);

		InstanceId id = codec.treeToValue(node.get("id"), InstanceId.class);
		State current = codec.treeToValue(node.get("current"), State.class);
		InstanceId parentId = null;

		if (node.hasNonNull("parentId"))
			parentId = codec.treeToValue(node.get("parentId"), InstanceId.class);

		int index = node.get("index").asInt();
		List<InstanceId> childsId = new ArrayList<>();
		JsonNode childsIdNode = node.get("childsIds");
		for (JsonNode childId : childsIdNode)
			childsId.add(codec.treeToValue(childId, InstanceId.class));

		Map<String, Object> data = adapter.deserialize(node.get("data"), codec);

		return AutomatInstanceValue.create(id, current, parentId, index, childsId, data);
	}

}
