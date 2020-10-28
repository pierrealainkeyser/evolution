package fr.keyser.evolution.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

public final class MetaCardId {

	private final String name;

	public MetaCardId(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	@JsonValue
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MetaCardId))
			return false;
		MetaCardId other = (MetaCardId) obj;
		return name == other.name;
	}
}
