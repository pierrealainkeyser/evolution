package fr.keyser.fsm.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;

import fr.keyser.fsm.impl.AutomatInstanceContainerValue;
import fr.keyser.fsm.impl.AutomatInstanceValue;

public class JsonDataMapAdapter {

	private final Map<String, Class<?>> accepted;

	public JsonDataMapAdapter(List<Class<?>> accepted) {
		this.accepted = accepted.stream().collect(Collectors.toMap(Class::getName, Function.identity()));
	}

	public SimpleModule asModule() {
		SimpleModule module = new SimpleModule("automats");
		module.addSerializer(new AutomatInstanceValueSerializer(this));
		module.addDeserializer(AutomatInstanceValue.class, new AutomatInstanceValueDeserializer(this));

		module.addSerializer(new AutomatInstanceContainerValueSerializer(this));
		module.addDeserializer(AutomatInstanceContainerValue.class,
				new AutomatInstanceContainerValueDeserializer(this));
		return module;
	}

	public void serialize(Map<String, Object> value, JsonGenerator gen) throws IOException {

		gen.writeStartArray();
		for (Entry<String, Object> e : value.entrySet()) {

			Object v = e.getValue();
			if (v != null && accepted.containsKey(v.getClass().getName())) {
				String cname = v.getClass().getName();
				gen.writeStartObject();
				gen.writeStringField("class", cname);
				gen.writeStringField("key", e.getKey());
				gen.writeObjectField("value", v);
				gen.writeEndObject();

			}
		}
		gen.writeEndArray();

	}

	public Map<String, Object> deserialize(JsonNode node, ObjectCodec codec) throws JsonProcessingException {

		Map<String, Object> out = new HashMap<>();
		for (JsonNode entry : node) {
			String classname = entry.get("class").asText();
			Class<?> type = accepted.get(classname);
			if (type != null) {
				String key = entry.get("key").asText();
				Object value = codec.treeToValue(entry.get("value"), type);
				out.put(key, value);
			}
		}

		return out;
	}

}
