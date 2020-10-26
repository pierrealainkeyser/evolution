package fr.keyser.fsm;

import java.util.Objects;

public final class AutomatEvent {

	private final Object key;

	private final Object payload;

	public AutomatEvent(Object key, Object payload) {
		this.key = key;
		this.payload = payload;
	}

	public Object getKey() {
		return key;
	}

	public Object getPayload() {
		return payload;
	}

	@Override
	public String toString() {
		String out = Objects.toString(key);
		if (payload == null)
			return out;
		else
			return out + "(*)";
	}

}
