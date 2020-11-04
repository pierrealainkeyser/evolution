package fr.keyser.evolution.exception;

import java.util.Map;

public abstract class EvolutionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4299962064182468076L;

	public EvolutionException(String message) {
		super(message);
	}

	public EvolutionException(String message, Throwable other) {
		super(message, other);
	}

	public void message(Map<String, Object> out) {
		out.put("code", getMessage());
	}

}
