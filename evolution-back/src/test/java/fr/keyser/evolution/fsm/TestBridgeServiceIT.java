package fr.keyser.evolution.fsm;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import fr.keyser.evolution.CoreConfiguration;
import fr.keyser.evolution.ServiceConfiguration;
import fr.keyser.evolution.command.AddCardToPoolCommand;
import fr.keyser.evolution.core.Card;
import fr.keyser.evolution.core.DeckBuilder;
import fr.keyser.evolution.fsm.TestBridgeServiceIT.InnerConf;
import fr.keyser.evolution.fsm.view.CompleteRender;
import fr.keyser.evolution.fsm.view.ViewDispatcher;
import fr.keyser.evolution.model.EvolutionGameSettings;
import fr.keyser.evolution.model.PlayerInputStatus;
import fr.keyser.evolution.model.Trait;
import fr.keyser.fsm.State;
import fr.keyser.security.AuthenticatedPlayer;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { InnerConf.class, CoreConfiguration.class, ServiceConfiguration.class })
public class TestBridgeServiceIT {
	
	private static final Logger logger = LoggerFactory.getLogger(TestBridgeServiceIT.class);

	@Configuration
	public static class InnerConf {
		@Bean
		public CachedGameResolver resolver() {
			return new CachedGameResolver(new MapGameResolver());

		}

	}

	@Autowired
	private BridgeService service;

	@MockBean
	private ViewDispatcher viewDispatcher;

	@Autowired
	private GameBuilder gameBuilder;

	@Autowired
	private GameResolver resolver;

	@Test
	void nominal() throws JsonProcessingException {
		
		ObjectMapper om = new ObjectMapper();
		ObjectWriter pp = om.writerWithDefaultPrettyPrinter();

		AuthenticatedPlayer ap0 = new AuthenticatedPlayer("p1", "Joueur 1");
		AuthenticatedPlayer ap1 = new AuthenticatedPlayer("p2", "Joueur 2");

		DeckBuilder deck = new DeckBuilder();
		deck.card(Trait.CARNIVOROUS);
		Card c2 = deck.card(Trait.COOPERATION);
		deck.card(Trait.BURROWING);
		deck.card(Trait.DEFENSIVE_HERDING);

		Card c4 = deck.card(Trait.HARD_SHELL);
		deck.card(Trait.FAT_TISSUE);
		deck.card(Trait.FERTILE);
		deck.card(Trait.FERTILE);

		ActiveGame created = gameBuilder.create(new EvolutionGameSettings(Arrays.asList(ap0, ap1), true), deck.deck());
		resolver.addGame(created, ap0);

		PlayerRef r0 = created.getPlayers().get(0);
		PlayerRef r1 = created.getPlayers().get(1);

		String p0 = r0.getUuid();
		String p1 = r1.getUuid();

		CompleteRender complete = service.connect(p0);
		assertThat(complete.getDraw())
				.isEqualTo(0);
		assertThat(complete.getGame().getPlayers())
				.hasSize(2)
				.allSatisfy(pv -> {
					assertThat(pv.getStatus()).isEqualTo(PlayerInputStatus.SELECT_FOOD);
				});

		service.selectFood(p0, new AddCardToPoolCommand(c2.getId()));

		verifiyDispatch(r0, r1);

		assertThat(resolver.getEngine(created.getRef()).get().getRoot().getCurrent())
				.isEqualTo(new State("control", "selectFood"));

		service.selectFood(p1, new AddCardToPoolCommand(c4.getId()));

		verifiyDispatch(r0, r1);

		complete = service.connect(p0);
		assertThat(complete.getGame().getPlayers())
				.hasSize(2)
				.allSatisfy(pv -> {
					assertThat(pv.getStatus()).isEqualTo(PlayerInputStatus.PLAY_CARDS);
				});
		
		logger.info("--->\n{}", pp.writeValueAsString(complete));

	}

	private void verifiyDispatch(PlayerRef r0, PlayerRef r1) {
		Mockito.verify(viewDispatcher).dispatch(Mockito.eq(r0), Mockito.any());
		Mockito.verify(viewDispatcher).dispatch(Mockito.eq(r1), Mockito.any());
		Mockito.reset(viewDispatcher);
	}

}
