package fr.keyser.evolution.fsm.jdbc;

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
import fr.keyser.evolution.fsm.GameRef;
import fr.keyser.evolution.fsm.ResolvedRef;
import fr.keyser.evolution.model.EvolutionGameSettings;
import fr.keyser.fsm.impl.AutomatEngine;

@JdbcTest
@ContextConfiguration(classes = { JacksonAutoConfiguration.class, CoreConfiguration.class, JacksonConfiguration.class,
		JdbcConfiguration.class })
public class TestJdcGameResolverIT {

	@Autowired
	private JdbcGameResolver jdbcGameResolver;

	@Autowired
	private GameBuilder gameBuilder;

	@Autowired
	private JdbcOperations jdbc;

	@Transactional
	@Test
	void nominal() {

		jdbc.update("insert into user(uid,name) values(?,?)", "pak", "pak");
		jdbc.update("insert into user(uid,name)  values(?,?)", "jmm", "jmm");

		EvolutionGameSettings settings = new EvolutionGameSettings(
				Arrays.asList(new AuthenticatedPlayer("pak", "pak"), new AuthenticatedPlayer("jmm", "jmm")), true);
		ActiveGame created = gameBuilder.create(settings);

		jdbcGameResolver.addGame(created);

		String myUuid = created.getPlayers().get(0).getUuid();
		ResolvedRef found = jdbcGameResolver.findByUuid(myUuid);
		GameRef foundRef = found.getGame();
		assertThat(foundRef.getUuid()).isEqualTo(created.getRef().getUuid());
		
		AutomatEngine engine = jdbcGameResolver.getEngine(foundRef);
		
		
		jdbcGameResolver.updateEngine(foundRef, engine);

	}
}
