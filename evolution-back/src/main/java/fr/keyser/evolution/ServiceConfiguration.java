package fr.keyser.evolution;

import java.util.function.Supplier;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.keyser.evolution.fsm.BridgeService;
import fr.keyser.evolution.fsm.GameLocker;
import fr.keyser.evolution.fsm.GameRef;
import fr.keyser.evolution.fsm.GameResolver;
import fr.keyser.evolution.fsm.view.Renderer;
import fr.keyser.evolution.fsm.view.ViewDispatcher;

@Configuration
public class ServiceConfiguration {

	@Bean
	public GameLocker locker() {
		return new GameLocker() {

			@Override
			public <T> T withinLock(GameRef game, Supplier<T> supplier) {
				return supplier.get();
			}
		};
	}

	@Bean
	public BridgeService bridgeService(GameLocker locker, GameResolver resolver, Renderer renderer,
			ViewDispatcher dispatcher) {
		return new BridgeService(locker, resolver, renderer, dispatcher);
	}
}
