package fr.keyser.evolution.fsm.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.keyser.evolution.fsm.ActiveGame;
import fr.keyser.evolution.fsm.AuthenticatedPlayer;
import fr.keyser.evolution.fsm.GameBuilder;
import fr.keyser.evolution.fsm.GameRef;
import fr.keyser.evolution.fsm.GameResolver;
import fr.keyser.evolution.fsm.PlayerRef;
import fr.keyser.evolution.fsm.ResolvedRef;
import fr.keyser.evolution.model.EvolutionGameParameters;
import fr.keyser.evolution.model.EvolutionGameSettings;
import fr.keyser.fsm.impl.AutomatEngine;
import fr.keyser.fsm.impl.AutomatInstanceContainerValue;

public class JdbcGameResolver implements GameResolver {

	private final JdbcOperations jdbc;

	private final ObjectMapper objectMapper;

	private final GameBuilder gameBuilder;

	public JdbcGameResolver(JdbcOperations jdbc, ObjectMapper objectMapper, GameBuilder gameBuilder) {
		this.jdbc = jdbc;
		this.objectMapper = objectMapper;
		this.gameBuilder = gameBuilder;
	}

	@Override
	public ResolvedRef findByUuid(String uuid) {

		String game = jdbc.queryForObject("select game from player where player=?", String.class, uuid);

		String sql = "select p.player, u.uid, u.name, p.index from player p inner join user u on u.uid=p.user where p.game =? order by p.index";
		List<PlayerRef> players = jdbc.query(sql, new Object[] { game }, this::toRef);
		return new ResolvedRef(players.stream().filter(p -> p.getUuid().equals(uuid)).findFirst().get(),
				new GameRef(game, players));
	}

	private PlayerRef toRef(ResultSet rs, int rowNum) throws SQLException {
		return new PlayerRef(rs.getInt("index"), rs.getString("player"),
				new AuthenticatedPlayer(rs.getString("uid"), rs.getString("name")));
	}

	@Override
	public AutomatEngine getEngine(GameRef ref) {

		String sql = "select content, quickplay from game where uuid=?";

		return jdbc.queryForObject(sql, new Object[] { ref.getUuid() }, (rs, count) -> {
			EvolutionGameParameters parameters = new EvolutionGameParameters(ref.getPlayersCount(),
					rs.getBoolean("quickplay"));

			AutomatInstanceContainerValue value = decode(
					AutomatInstanceContainerValue.class, rs.getString("content"));

			return new AutomatEngine(value, gameBuilder.createLogic(parameters));
		});
	}

	private <T> T decode(Class<T> type, String content) {
		T value;
		try {
			value = objectMapper.readValue(content, type);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("illegal content format", e);
		}
		return value;
	}

	@Override
	@Transactional
	public void updateEngine(GameRef ref, AutomatEngine engine) {

		AutomatInstanceContainerValue internal = engine.getInternal();
		boolean terminated = gameBuilder.isTerminated(engine);
		jdbc.update("update game set content=?, terminated=? where uuid=?",
				encode(internal), terminated, ref.getUuid());

		if (terminated) {
			// TODO update score
		}

	}

	private String encode(Object internal) {
		String encode = null;
		try {
			encode = objectMapper.writeValueAsString(internal);

		} catch (JsonProcessingException e) {
			throw new RuntimeException("illegal content format", e);
		}
		return encode;
	}

	@Override
	@Transactional
	public void addGame(ActiveGame active) {
		EvolutionGameSettings settings = active.getSettings();

		String sql = "insert into game(uuid, content, traits,quickplay, terminated) values (?,?,?,?,?)";
		GameRef ref = active.getRef();
		String content = encode(active.getEngine().getInternal());

		jdbc.update(sql, ref.getUuid(),
				content, encode(settings.getTraits()), settings.isQuickplay(), false);

		sql = "insert into player (player,user,game,index) values(?,?,?,?)";
		jdbc.batchUpdate(sql,
				ref.getPlayers().stream()
						.map(p -> new Object[] { p.getUuid(), p.getUserId(), ref.getUuid(), p.getIndex() })
						.collect(Collectors.toList()));
	}

}
