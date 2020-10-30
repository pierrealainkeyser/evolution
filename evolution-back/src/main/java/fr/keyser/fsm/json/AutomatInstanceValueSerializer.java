package fr.keyser.fsm.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import fr.keyser.fsm.InstanceId;
import fr.keyser.fsm.impl.AutomatInstanceValue;

public class AutomatInstanceValueSerializer extends StdSerializer<AutomatInstanceValue> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6989477813667141634L;

	private final JsonDataMapAdapter adapter;

	public AutomatInstanceValueSerializer(JsonDataMapAdapter adapter) {
		super(AutomatInstanceValue.class);
		this.adapter = adapter;
	}

	@Override
	public void serialize(AutomatInstanceValue value, JsonGenerator gen, SerializerProvider provider)
			throws IOException {

		gen.writeStartObject();
		gen.writeObjectField("id", value.getId());
		gen.writeObjectField("current", value.getCurrent());
		InstanceId parentId = value.getParentId();
		if (parentId != null) {
			gen.writeObjectField("parentId", parentId);
		}
		gen.writeNumberField("index", value.getIndex());

		gen.writeArrayFieldStart("childsIds");
		for (InstanceId child : value.getChildsId()) {
			gen.writeObject(child);
		}

		gen.writeEndArray();

		gen.writeFieldName("data");
		adapter.serialize(value.getData(), gen);

		gen.writeEndObject();

	}

}
