package fr.keyser.evolution.web;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessageSendingOperations;

import fr.keyser.evolution.fsm.PlayerRef;
import fr.keyser.evolution.fsm.view.PartialRender;
import fr.keyser.evolution.fsm.view.ViewDispatcher;
import fr.keyser.evolution.overview.GameOverview;
import fr.keyser.evolution.overview.OverviewDispatcher;

public class SimpMessageViewDispatcher implements ViewDispatcher, OverviewDispatcher {

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

	@Override
	public void dispatch(String type, List<GameOverview> overviews) {
		for (GameOverview go : overviews) {
			sendingOperations.convertAndSendToUser(go.getUser(), "/my-games", Map.of("type", type, "game", go));
		}
	}
}
