package fr.keyser.security;

import java.security.Principal;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticatedPlayer implements Principal {

	private final String id;

	private final String name;

	@JsonCreator
	public AuthenticatedPlayer(@JsonProperty("id") String id, @JsonProperty("name") String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return String.format("%s aka '%s'", id, name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AuthenticatedPlayer))
			return false;
		AuthenticatedPlayer other = (AuthenticatedPlayer) obj;
		return Objects.equals(id, other.id) && Objects.equals(name, other.name);
	}
}
