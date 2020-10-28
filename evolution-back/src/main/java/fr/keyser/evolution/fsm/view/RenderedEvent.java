package fr.keyser.evolution.fsm.view;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

public class RenderedEvent {

	private final String type;

	private final Map<String, Object> objets = new LinkedHashMap<>();

	public RenderedEvent(String type) {
		this.type = type;
	}

	public RenderedEvent put(String key, Object value) {
		objets.put(key, value);
		return this;
	}
	

	@JsonAnyGetter
	public Map<String, Object> getObjets() {
		return objets;
	}

	public String getType() {
		return type;
	}

}
