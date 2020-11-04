package fr.keyser.evolution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.keyser.security.AuthenticatedPlayerConverter;
import fr.keyser.security.AuthenticatedPlayerRepository;
import fr.keyser.security.AuthenticatedPlayerRepositoryListener;

@Configuration
public class AuthenticatedPlayerConfiguration {

	@Bean
	public AuthenticatedPlayerRepositoryListener authenticatedPlayerRepositoryListener(
			AuthenticatedPlayerConverter converter,
			AuthenticatedPlayerRepository authenticatedPlayerRepository) {
		return new AuthenticatedPlayerRepositoryListener(converter, authenticatedPlayerRepository);
	}

	@Bean
	public AuthenticatedPlayerConverter authenticatedPlayerConverter() {
		return new AuthenticatedPlayerConverter();
	}
}
