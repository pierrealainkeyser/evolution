package fr.keyser.evolution;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import fr.keyser.security.AuthenticatedPlayerConverter;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

	@Autowired
	private AuthenticatedPlayerConverter converter;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {

		config.setApplicationDestinationPrefixes("/app");
		config.setUserDestinationPrefix("/user");

		config.enableSimpleBroker().setTaskScheduler(new ConcurrentTaskScheduler());
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws").setAllowedOrigins("*").setHandshakeHandler(new DefaultHandshakeHandler() {
			@Override
			protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
					Map<String, Object> attributes) {
				return converter.convert(request.getPrincipal());
			}
		});
	}
}