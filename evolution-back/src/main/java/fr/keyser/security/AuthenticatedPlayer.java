package fr.keyser.security;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticatedPlayer implements Principal, Authentication {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8273583645282183239L;

	private final String label;

	private final String name;

	@JsonCreator
	public AuthenticatedPlayer(@JsonProperty("name") String name, @JsonProperty("label") String label) {
		this.label = label;
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return String.format("%s aka '%s'", name, label);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
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
		return Objects.equals(name, other.name);
	}

	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	@Override
	@JsonIgnore
	public Object getCredentials() {
		return null;
	}

	@Override
	@JsonIgnore
	public Object getDetails() {
		return null;
	}

	@Override
	@JsonIgnore
	public Object getPrincipal() {
		return name;
	}

	@Override
	@JsonIgnore
	public boolean isAuthenticated() {
		return true;
	}

	@Override
	@JsonIgnore
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

	}
}
