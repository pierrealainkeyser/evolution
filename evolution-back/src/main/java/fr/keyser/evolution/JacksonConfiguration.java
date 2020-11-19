package fr.keyser.evolution;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.module.SimpleModule;

import fr.keyser.evolution.core.PlayerState;
import fr.keyser.evolution.core.json.CoreModule;
import fr.keyser.evolution.fsm.PlayAreaMonitor;
import fr.keyser.evolution.model.PlayersScoreBoard;
import fr.keyser.evolution.summary.FeedingActionSummaries;
import fr.keyser.fsm.json.JsonDataMapAdapter;

@Configuration
public class JacksonConfiguration {

	@Bean
	public SimpleModule automatsModule() {
		JsonDataMapAdapter adapter = new JsonDataMapAdapter(
				Arrays.asList(PlayAreaMonitor.class, PlayersScoreBoard.class, PlayerState.class,
						FeedingActionSummaries.class));
		return adapter.asModule();
	}

	@Bean
	public CoreModule coreModule() {
		return new CoreModule();
	}

}
