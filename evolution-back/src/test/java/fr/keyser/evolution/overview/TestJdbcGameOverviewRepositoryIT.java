package fr.keyser.evolution.overview;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fr.keyser.evolution.CoreConfiguration;
import fr.keyser.evolution.JacksonConfiguration;
import fr.keyser.evolution.JdbcConfiguration;
import fr.keyser.evolution.fsm.ActiveGame;
import fr.keyser.evolution.fsm.GameBuilder;
import fr.keyser.evolution.fsm.jdbc.JdbcGameResolver;
import fr.keyser.evolution.model.EvolutionGameSettings;
import fr.keyser.security.AuthenticatedPlayer;
import fr.keyser.security.AuthenticatedPlayerRepository;

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
	private AuthenticatedPlayerRepository playerRepository;

	@Transactional
	@Test
	void nominal() {
		AuthenticatedPlayer owner = new AuthenticatedPlayer("pak", "PAK");
		AuthenticatedPlayer other = new AuthenticatedPlayer("jmm", "JMM");

		playerRepository.add(owner);
		playerRepository.add(other);

		EvolutionGameSettings settings = new EvolutionGameSettings(Arrays.asList(owner, other), true);
		ActiveGame created = gameBuilder.create(settings);

		jdbcGameResolver.addGame(created, owner);

		assertThat(repository.myGames(owner))
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
