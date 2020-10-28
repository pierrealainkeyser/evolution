package fr.keyser.evolution.fsm.view;

public class CompleteRender extends Render {
	
	public CompleteRender(int draw) {
		super(draw);
	}

	@Override
	public String getType() {
		return "complete";
	}
}
