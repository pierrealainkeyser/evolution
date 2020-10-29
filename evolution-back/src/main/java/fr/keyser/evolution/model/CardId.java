package fr.keyser.evolution.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

public final class CardId {

	public static CardId parse(String s) {
		return new CardId(Integer.parseInt(s.substring(1)));
	}

	private final int index;

	public CardId(int index) {
		this.index = index;
	}

	@Override
	public int hashCode() {
		return Objects.hash(index);
	}

	@JsonValue
	@Override
	public String toString() {
		return "#" + index;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CardId))
			return false;
		CardId other = (CardId) obj;
		return index == other.index;
	}
}
