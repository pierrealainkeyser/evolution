package fr.keyser.evolution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.keyser.evolution.fsm.GameBuilder;
import fr.keyser.evolution.fsm.jdbc.JdbcGameResolver;

@Configuration
public class JdbcConfiguration {

	@Bean
	public JdbcGameResolver jdbcGameResolver(JdbcOperations jdbc, ObjectMapper objectMapper, GameBuilder gameBuilder) {
		return new JdbcGameResolver(jdbc, objectMapper, gameBuilder);
	}
}
