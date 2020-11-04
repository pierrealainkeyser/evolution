package fr.keyser.evolution.exception;

import java.util.Map;

import fr.keyser.evolution.model.CardId;

public class IllegalCardException extends EvolutionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4694555817375667960L;
	private final CardId cardId;

	public IllegalCardException(CardId command) {
		super("illegal_card");
		this.cardId = command;
	}

	@Override
	public void message(Map<String, Object> out) {
		super.message(out);
		out.put("cardId", cardId);
	}

}
