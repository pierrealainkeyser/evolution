package fr.keyser.evolution.fsm.view;

public abstract class Render {

	private final int draw;

	Render(int draw) {
		this.draw = draw;
	}

	public abstract String getType();

	public final int getDraw() {
		return draw;
	}
}
