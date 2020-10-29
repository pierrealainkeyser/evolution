package fr.keyser.evolution.fsm;

public class PlayerRef {

	private final int index;

	private final String uuid;

	private final AuthenticatedPlayer player;

	public PlayerRef(int index, String uuid, AuthenticatedPlayer player) {
		this.index = index;
		this.uuid = uuid;
		this.player = player;
	}

	public int getIndex() {
		return index;
	}

	public String getUuid() {
		return uuid;
	}

	public AuthenticatedPlayer getPlayer() {
		return player;
	}

	@Override
	public String toString() {
		return String.format("PlayerRef [index=%s, uuid=%s, player=%s]", index, uuid, player);
	}

	public String getId() {
		return player.getId();
	}
}
