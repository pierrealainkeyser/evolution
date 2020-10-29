package fr.keyser.evolution.fsm;

public class AuthenticatedPlayer {

	private final String id;

	private final String name;

	public AuthenticatedPlayer(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return String.format("AuthenticatedPlayer [id=%s, name=%s]", id, name);
	}
}
