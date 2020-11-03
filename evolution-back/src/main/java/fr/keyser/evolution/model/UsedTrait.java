package fr.keyser.evolution.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UsedTrait {

	private final SpecieId specie;

	private final Trait trait;

	@JsonCreator
	public UsedTrait(@JsonProperty("specie") SpecieId specie, @JsonProperty("trait") Trait trait) {
		this.specie = specie;
		this.trait = trait;
	}

	public SpecieId getSpecie() {
		return specie;
	}

	public Trait getTrait() {
		return trait;
	}

	@Override
	public String toString() {
		return String.format("%s@%s", trait, specie);
	}

	@Override
	public int hashCode() {
		return Objects.hash(specie, trait);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UsedTrait))
			return false;
		UsedTrait other = (UsedTrait) obj;
		return Objects.equals(specie, other.specie) && trait == other.trait;
	}
}
