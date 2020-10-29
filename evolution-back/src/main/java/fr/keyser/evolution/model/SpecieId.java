package fr.keyser.evolution.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

public class SpecieId {

	private final int id;

	private final int player;

	public SpecieId(int id, int player) {
		this.id = id;
		this.player = player;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SpecieId))
			return false;
		SpecieId other = (SpecieId) obj;
		return id == other.id && player == other.player;
	}

	public int getId() {
		return id;
	}

	public int getPlayer() {
		return player;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, player);
	}

	@JsonValue
	@Override
	public String toString() {
		return String.format("%sp%s", id, player);
	}

}
