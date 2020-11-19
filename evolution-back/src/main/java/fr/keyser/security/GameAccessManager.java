package fr.keyser.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import fr.keyser.evolution.fsm.GameResolver;
import fr.keyser.evolution.fsm.ResolvedRef;

public class GameAccessManager {

	private final GameResolver gameResolver;

	private final AuthenticatedPlayerConverter converter;

	public GameAccessManager(GameResolver gameResolver, AuthenticatedPlayerConverter converter) {
		this.gameResolver = gameResolver;
		this.converter = converter;
	}

	public boolean hasAccess(String uuid) {
		try {
			ResolvedRef found = gameResolver.findByUuid(uuid);
			if (found != null) {

				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				AuthenticatedPlayer player = converter.convert(auth);
				if (player != null) {
					return found.getMyself().getUser().equals(player);
				}

			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
}
