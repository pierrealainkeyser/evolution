package fr.keyser.security;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

public class AuthenticatedPlayerRepositoryListener {

	private final AuthenticatedPlayerConverter converter;

	private final AuthenticatedPlayerRepository authenticatedPlayerRepository;

	public AuthenticatedPlayerRepositoryListener(AuthenticatedPlayerConverter converter,
			AuthenticatedPlayerRepository authenticatedPlayerRepository) {
		this.converter = converter;
		this.authenticatedPlayerRepository = authenticatedPlayerRepository;
	}

	@EventListener
	public void eventListener(AuthenticationSuccessEvent event) {
		Authentication authentication = event.getAuthentication();
		AuthenticatedPlayer pp = converter.convert(authentication);
		if (pp != null)
			authenticatedPlayerRepository.add(pp);
	}

}
