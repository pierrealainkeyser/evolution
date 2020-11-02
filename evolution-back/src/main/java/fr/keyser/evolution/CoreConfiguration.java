package fr.keyser.evolution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.keyser.evolution.fsm.GameBuilder;
import fr.keyser.evolution.fsm.view.Renderer;

@Configuration
public class CoreConfiguration {

	@Bean
	public GameBuilder gameBuilder() {
		return new GameBuilder();
	}

	@Bean
	public Renderer renderer() {
		return new Renderer();
	}
}
