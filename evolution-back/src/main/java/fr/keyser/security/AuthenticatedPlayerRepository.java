package fr.keyser.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.transaction.annotation.Transactional;

public class AuthenticatedPlayerRepository {

	private static final Logger logger = LoggerFactory.getLogger(AuthenticatedPlayerRepository.class);

	private final JdbcOperations jdbc;

	public AuthenticatedPlayerRepository(JdbcOperations jdbc) {
		this.jdbc = jdbc;
	}

	@Transactional
	public void add(AuthenticatedPlayer user) {
		jdbc.update("insert into user(uid, name) select ?,? where not exists (select * from user where uid=?)",
				user.getName(), user.getLabel(), user.getName());

		logger.info("Adding user : {}", user);
	}
}
