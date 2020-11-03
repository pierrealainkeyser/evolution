package fr.keyser.evolution.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.keyser.evolution.model.SpecieId;

public class FatMoved extends SpecieEvent {

	private final int fat;

	@JsonCreator
	public FatMoved(@JsonProperty("src") SpecieId src, @JsonProperty("fat") int fat) {
		super(src);
		this.fat = fat;
	}

	public int getFat() {
		return fat;
	}
}
