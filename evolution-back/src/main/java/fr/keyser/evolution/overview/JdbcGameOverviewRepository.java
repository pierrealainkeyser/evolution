package fr.keyser.evolution.overview;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcOperations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.keyser.evolution.model.Trait;

public class JdbcGameOverviewRepository implements GameOverviewRepository {

	private final static TypeReference<List<Trait>> TRAITS_TYPE = new TypeReference<List<Trait>>() {
	};

	private final JdbcOperations jdbc;

	private final ObjectMapper objectMapper;

	public JdbcGameOverviewRepository(JdbcOperations jdbc, ObjectMapper objectMapper) {
		this.jdbc = jdbc;
		this.objectMapper = objectMapper;
	}

	private List<Trait> traits(String traits) {
		try {

			return objectMapper.readValue(traits, TRAITS_TYPE);
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	@Override
	public List<GameOverview> myGames(String userId) {

		String sql = "select  g.uuid, g.created, g.quickplay, g.traits, g.terminated,p.player,  p.score, p.alpha  from game g inner join player p on g.uuid=p.game where p.user=?";

		List<GameOverview> games = jdbc.query(sql, new Object[] { userId }, (rs, i) -> {

			Instant created = rs.getTimestamp("created").toInstant();
			String game = rs.getString("uuid");
			String playerUUID = rs.getString("player");
			boolean quickplay = rs.getBoolean("quickplay");
			List<Trait> traits = traits(rs.getString("traits"));
			List<String> players = new ArrayList<>();
			boolean terminated = rs.getBoolean("terminated");
			Integer score = null;
			Boolean alpha = null;

			if (terminated) {
				score = rs.getInt("score");
				alpha = rs.getBoolean("alpha");
			}

			return new GameOverview(created, game, playerUUID, quickplay, traits, players, terminated, score, alpha);
		});

		Map<String, GameOverview> byId = games.stream()
				.collect(Collectors.toMap(GameOverview::getGame, Function.identity()));

		sql = "select g.uuid, u.name from game g inner join player p on g.uuid=p.game inner join user u on p.user=u.uid where g.uuid in (select game from player where user=?)";
		jdbc.query(sql, new Object[] { userId }, rs -> {
			String game = rs.getString("uuid");
			String name = rs.getString("name");

			GameOverview go = byId.get(game);
			if (go != null)
				go.getPlayers().add(name);
		});

		return games;
	}
}
