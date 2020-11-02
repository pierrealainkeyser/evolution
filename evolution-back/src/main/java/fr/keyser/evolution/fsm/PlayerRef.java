package fr.keyser.evolution.fsm;

public class PlayerRef {

	private final int index;

	private final String uuid;

	private final AuthenticatedPlayer user;

	public PlayerRef(int index, String uuid, AuthenticatedPlayer user) {
		this.index = index;
		this.uuid = uuid;
		this.user = user;
	}

	public int getIndex() {
		return index;
	}

	public String getUuid() {
		return uuid;
	}

	public AuthenticatedPlayer getUser() {
		return user;
	}

	@Override
	public String toString() {
		return String.format("PlayerRef [index=%s, uuid=%s, user=%s]", index, uuid, user);
	}

	public String getUserId() {
		return user.getId();
	}
}
