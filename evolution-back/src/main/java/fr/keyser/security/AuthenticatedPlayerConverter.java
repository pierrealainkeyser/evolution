package fr.keyser.security;

import java.security.Principal;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class AuthenticatedPlayerConverter {

	public AuthenticatedPlayer convert(Principal principal) {
		if (principal instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
			String client = token.getAuthorizedClientRegistrationId();
			return convert(client, token.getPrincipal());
		}

		if (principal instanceof OAuth2LoginAuthenticationToken) {
			OAuth2LoginAuthenticationToken token = (OAuth2LoginAuthenticationToken) principal;
			String client = token.getClientRegistration().getRegistrationId();
			return convert(client, token.getPrincipal());
		}

		if (principal instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
			String name = token.getName();

			return new AuthenticatedPlayer(name + "@form", name);
		}

		if (principal instanceof AuthenticatedPlayer) {
			return (AuthenticatedPlayer) principal;
		}

		return null;
	}

	private AuthenticatedPlayer convert(String client, OAuth2User user) {
		String id = null;
		if ("google".equals(client)) {
			id = user.getAttribute("sub");
		} else if ("github".equals(client)) {
			id = user.getAttribute("login");
		}
		return new AuthenticatedPlayer(id + "@" + client, user.getAttribute("name"));

	}
}
