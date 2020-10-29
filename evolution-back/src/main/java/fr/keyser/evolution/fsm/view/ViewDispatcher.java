package fr.keyser.evolution.fsm.view;

import fr.keyser.evolution.fsm.PlayerRef;

public interface ViewDispatcher {

	public void dispatch(PlayerRef ref, PartialRender render);
}
