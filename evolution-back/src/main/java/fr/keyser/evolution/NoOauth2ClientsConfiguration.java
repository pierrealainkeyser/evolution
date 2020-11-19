package fr.keyser.evolution;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import fr.keyser.security.ClientsNotConfiguredCondition;

@Configuration
@Conditional(ClientsNotConfiguredCondition.class)
public class NoOauth2ClientsConfiguration {

	@ConditionalOnMissingBean(ClientRegistrationRepository.class)
	@Bean
	public ClientRegistrationRepository clientRegistrationRepository() {
		return new ClientRegistrationRepository() {

			@Override
			public ClientRegistration findByRegistrationId(String registrationId) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
}
