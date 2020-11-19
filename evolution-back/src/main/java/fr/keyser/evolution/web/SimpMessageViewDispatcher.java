package fr.keyser.evolution.web;

import java.text.MessageFormat;

import org.springframework.messaging.simp.SimpMessageSendingOperations;

import fr.keyser.evolution.fsm.PlayerRef;
import fr.keyser.evolution.fsm.view.PartialRender;
import fr.keyser.evolution.fsm.view.ViewDispatcher;

public class SimpMessageViewDispatcher implements ViewDispatcher {

	private final SimpMessageSendingOperations sendingOperations;

	public SimpMessageViewDispatcher(SimpMessageSendingOperations sendingOperations) {
		this.sendingOperations = sendingOperations;
	}

	@Override
	public void dispatch(PlayerRef ref, PartialRender render) {
		String destination = MessageFormat.format("/game/{0}", ref.getUuid());
		String userId = ref.getUserId();
		sendingOperations.convertAndSendToUser(userId, destination, render);
	}
}
