package fr.keyser.fsm.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import fr.keyser.fsm.impl.AutomatInstanceContainerValue;

public class AutomatInstanceContainerValueSerializer extends StdSerializer<AutomatInstanceContainerValue> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6989477813667141634L;

	private final JsonDataMapAdapter adapter;

	public AutomatInstanceContainerValueSerializer(JsonDataMapAdapter adapter) {
		super(AutomatInstanceContainerValue.class);
		this.adapter = adapter;
	}

	@Override
	public void serialize(AutomatInstanceContainerValue value, JsonGenerator gen, SerializerProvider provider)
			throws IOException {

		gen.writeStartObject();
		gen.writeObjectField("all", value.getAll());
		gen.writeFieldName("data");
		adapter.serialize(value.getData(), gen);
		gen.writeEndObject();

	}

}
