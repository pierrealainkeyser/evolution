package fr.keyser.evolution.fsm;

public class PlayerRef {

	private final int index;

	private final String uuid;

	private final String label;

	public PlayerRef(int index, String uuid, String label) {
		this.index = index;
		this.uuid = uuid;
		this.label = label;
	}

	public int getIndex() {
		return index;
	}

	public String getUuid() {
		return uuid;
	}

	public String getLabel() {
		return label;
	}
}
