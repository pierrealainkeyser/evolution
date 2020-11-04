package fr.keyser.evolution.overview;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fr.keyser.evolution.CoreConfiguration;
import fr.keyser.evolution.JacksonConfiguration;
import fr.keyser.evolution.JdbcConfiguration;
import fr.keyser.evolution.fsm.ActiveGame;
import fr.keyser.evolution.fsm.AuthenticatedPlayer;
import fr.keyser.evolution.fsm.GameBuilder;
import fr.keyser.evolution.fsm.jdbc.JdbcGameResolver;
import fr.keyser.evolution.model.EvolutionGameSettings;

@JdbcTest
@ContextConfiguration(classes = { JacksonAutoConfiguration.class, CoreConfiguration.class, JacksonConfiguration.class,
		JdbcConfiguration.class })
public class TestJdbcGameOverviewRepositoryIT {

	@Autowired
	private JdbcGameOverviewRepository repository;

	@Autowired
	private JdbcGameResolver jdbcGameResolver;
	@Autowired
	private GameBuilder gameBuilder;

	@Autowired
	private JdbcOperations jdbc;

	@Transactional
	@Test
	void nominal() {

		jdbc.update("insert into user(uid,name) values(?,?)", "pak", "PAK");
		jdbc.update("insert into user(uid,name)  values(?,?)", "jmm", "JMM");

		EvolutionGameSettings settings = new EvolutionGameSettings(
				Arrays.asList(new AuthenticatedPlayer("pak", "PAK"), new AuthenticatedPlayer("jmm", "JMM")), true);
		ActiveGame created = gameBuilder.create(settings);

		jdbcGameResolver.addGame(created);

		assertThat(repository.myGames("pak"))
				.hasSize(1)
				.anySatisfy(go -> {
					assertThat(go.isTerminated()).isFalse();
					assertThat(go.getTraits()).isEmpty();
					assertThat(go.getPlayers())
							.hasSize(2)
							.contains("PAK", "JMM");

				});

	}
}
