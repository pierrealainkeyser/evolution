package fr.keyser.evolution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

import fr.keyser.evolution.fsm.GameResolver;
import fr.keyser.security.AuthenticatedPlayerConverter;
import fr.keyser.security.GameAccessManager;

@Configuration
public class WebSocketSecurityConfiguration extends AbstractSecurityWebSocketMessageBrokerConfigurer {

	@Bean
	public GameAccessManager gameAccessManager(GameResolver gameResolver, AuthenticatedPlayerConverter converter) {
		return new GameAccessManager(gameResolver, converter);
	}

	@Override
	protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {

		messages.simpSubscribeDestMatchers("/app/game/{uuid}/**")
				.access("@gameAccessManager.hasAccess(#uuid)");
		messages.simpSubscribeDestMatchers("/user/game/{uuid}/**")
				.access("@gameAccessManager.hasAccess(#uuid)");

		messages.simpMessageDestMatchers("/app/game/{uuid}/**").access("@gameAccessManager.hasAccess(#uuid)");
		

	}
}