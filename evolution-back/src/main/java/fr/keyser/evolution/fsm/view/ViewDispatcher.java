package fr.keyser.evolution.fsm.view;

public interface ViewDispatcher {

	public void dispatch(String uuid, Render render);
}
