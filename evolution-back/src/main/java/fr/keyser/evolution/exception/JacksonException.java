package fr.keyser.evolution.exception;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

public class JacksonException extends EvolutionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4694555817375667960L;
	private final String message;

	public JacksonException(JsonProcessingException ex) {
		super("invalid_content");
		this.message = ex.getMessage();
	}

	@Override
	public void message(Map<String, Object> out) {
		super.message(out);
		out.put("message", message);
	}

}
