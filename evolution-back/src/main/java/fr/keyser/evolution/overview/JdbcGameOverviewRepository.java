package fr.keyser.evolution.overview;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcOperations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.keyser.evolution.fsm.GameRef;
import fr.keyser.evolution.model.Trait;
import fr.keyser.security.AuthenticatedPlayer;

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
	public List<GameOverview> overview(GameRef ref) {
		return loadOverviews("p.game=?", ref.getUuid());
	}

	@Override
	public List<GameOverview> myGames(AuthenticatedPlayer player) {
		return loadOverviews("p.user=?", player.getName());
	}

	private List<GameOverview> loadOverviews(String filter, Object... filterValue) {
		String sql = "select  g.uuid, g.created, g.quickplay, g.traits, g.terminated,p.player,p.user,  p.score, p.alpha  from game g inner join player p on g.uuid=p.game where "
				+ filter;
		List<GameOverview> games = jdbc.query(sql, filterValue, (rs, i) -> {

			Instant created = rs.getTimestamp("created").toInstant();
			String game = rs.getString("uuid");
			String user = rs.getString("user");
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

			return new GameOverview(created, game, user, playerUUID, quickplay, traits, players, terminated, score,
					alpha);
		});

		Map<String, List<GameOverview>> byId = games.stream()
				.collect(Collectors.groupingBy(GameOverview::getGame));

		sql = "select g.uuid, u.name from game g inner join player p on g.uuid=p.game inner join user u on p.user=u.uid where g.uuid in (select game from player p where "
				+ filter + ")";
		jdbc.query(sql, filterValue, rs -> {
			String game = rs.getString("uuid");
			String name = rs.getString("name");

			List<GameOverview> gos = byId.get(game);
			if (gos != null)
				gos.forEach(go -> go.getPlayers().add(name));
		});

		return games;
	}
}
