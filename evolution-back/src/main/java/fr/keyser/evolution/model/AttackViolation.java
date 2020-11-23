package fr.keyser.evolution.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AttackViolation {

	private final String type;

	private final UsedTrait trait;

	private final UsedTrait bypass;

	private final AttackViolationStatus status;

	
	@JsonCreator
	public AttackViolation(@JsonProperty("type") String type,@JsonProperty("trait") UsedTrait trait,
			@JsonProperty("status") AttackViolationStatus status, @JsonProperty("bypass") UsedTrait bypass) {
		this.type = type;
		this.trait = trait;
		this.bypass = bypass;
		this.status = status;
	}

	public UsedTrait getTrait() {
		return trait;
	}

	public String getType() {
		return type;
	}

	public UsedTrait getBypass() {
		return bypass;
	}

	public AttackViolationStatus getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return String.format("AttackViolationView [type=%s, trait=%s, bypass=%s, status=%s]", type, trait, bypass, status);
	}

}
