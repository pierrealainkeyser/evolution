package fr.keyser.evolution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcOperations;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.keyser.evolution.fsm.CachedGameResolver;
import fr.keyser.evolution.fsm.GameBuilder;
import fr.keyser.evolution.fsm.jdbc.JdbcGameResolver;
import fr.keyser.evolution.overview.JdbcGameOverviewRepository;

@Configuration
public class JdbcConfiguration {

	@Bean
	public JdbcGameOverviewRepository jdbcGameOverviewRepository(JdbcOperations jdbc, ObjectMapper objectMapper) {
		return new JdbcGameOverviewRepository(jdbc, objectMapper);
	}

	@Bean
	public JdbcGameResolver jdbcGameResolver(JdbcOperations jdbc, ObjectMapper objectMapper, GameBuilder gameBuilder) {
		return new JdbcGameResolver(jdbc, objectMapper, gameBuilder);
	}

	@Bean
	@Primary
	public CachedGameResolver cacheGameResolver(JdbcGameResolver jdbcGameResolver) {
		return new CachedGameResolver(jdbcGameResolver);
	}
}
