package fr.keyser.evolution.fsm.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import fr.keyser.evolution.core.Card;
import fr.keyser.evolution.core.PlayArea;
import fr.keyser.evolution.core.Player;
import fr.keyser.evolution.core.PlayerState;
import fr.keyser.evolution.core.TurnStatus;
import fr.keyser.evolution.engine.Event;
import fr.keyser.evolution.event.Attacked;
import fr.keyser.evolution.event.CardAddedToPool;
import fr.keyser.evolution.event.CardDealed;
import fr.keyser.evolution.event.DiscardPoolFood;
import fr.keyser.evolution.event.FatMoved;
import fr.keyser.evolution.event.FoodEaten;
import fr.keyser.evolution.event.FoodScored;
import fr.keyser.evolution.event.LastTurnEvent;
import fr.keyser.evolution.event.NewTurnEvent;
import fr.keyser.evolution.event.NextStepEvent;
import fr.keyser.evolution.event.PlayerPassedEvent;
import fr.keyser.evolution.event.PlayerStateChanged;
import fr.keyser.evolution.event.PoolRevealed;
import fr.keyser.evolution.event.PopulationGrow;
import fr.keyser.evolution.event.PopulationIncreased;
import fr.keyser.evolution.event.PopulationReduced;
import fr.keyser.evolution.event.SizeIncreased;
import fr.keyser.evolution.event.SpecieAdded;
import fr.keyser.evolution.event.SpecieExtincted;
import fr.keyser.evolution.event.TraitAdded;
import fr.keyser.evolution.event.TraitsRevealed;
import fr.keyser.evolution.fsm.EvolutionGraphBuilder;
import fr.keyser.evolution.fsm.PlayAreaMonitor;
import fr.keyser.evolution.fsm.PlayerRef;
import fr.keyser.evolution.model.FoodConsumption;
import fr.keyser.evolution.model.FoodSource;
import fr.keyser.evolution.model.PlayersScoreBoard;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.UsedTrait;
import fr.keyser.evolution.summary.AttackOutcome;
import fr.keyser.evolution.summary.AttackSummary;
import fr.keyser.evolution.summary.FeedSummary;
import fr.keyser.evolution.summary.FeedingActionSummaries;
import fr.keyser.evolution.summary.FeedingActionSummary;
import fr.keyser.evolution.summary.IntelligentFeedSummary;
import fr.keyser.fsm.AutomatInstance;

public class Renderer {

	public CompleteRender complete(int player, List<PlayerRef> players, AutomatInstance playerInstance) {
		PlayAreaMonitor monitor = playerInstance.getGlobal(EvolutionGraphBuilder.PLAY_AREA);
		PlayersScoreBoard scoreBoards = playerInstance.getGlobal(EvolutionGraphBuilder.SCOREBOARDS);
		FeedingActionSummaries actions = playerInstance.getLocal(EvolutionGraphBuilder.FEEDING_ACTIONS);
		AutomatInstance root = playerInstance.getParent().get();

		int draw = monitor.getDraw();
		PlayArea area = monitor.getArea();
		TurnStatus ts = area.getTurnStatus();

		List<PlayerView> playersView = players.stream()
				.map(p -> renderPlayer(player, p, area, root.getChilds().get(p.getIndex())))
				.collect(Collectors.toList());
		PlayerAreaView playerAreaView = new PlayerAreaView(playersView, ts.getFirstPlayer(),
				scoreBoards != null ? scoreBoards.getBoards() : null, ts.getStep(), ts.isLastTurn(),
				new FoodPoolView(area.getPool()));

		Player myself = area.getPlayer(player);
		UserView userView = new UserView(player,
				myself.getHands().stream().map(CardView::new).collect(Collectors.toList()), actions(player, actions));

		return new CompleteRender(draw, userView, playerAreaView, renderAll(player, monitor.getHistory()));
	}

	private PlayerView renderPlayer(int forPlayer, PlayerRef ref, PlayArea area, AutomatInstance instance) {

		PlayerState state = instance.getLocal(EvolutionGraphBuilder.STATUS);
		int index = ref.getIndex();
		Player player = area.getPlayer(index);
		List<SpecieView> species = area.forPlayer(index).stream().map(s -> new SpecieView(s, index == forPlayer))
				.collect(Collectors.toList());
		return new PlayerView(index, ref.getUser(), state.getState(), player.getHandSize(), species);

	}

	public PartialRender partial(int player, AutomatInstance playerInstance, List<Event> events) {
		PlayAreaMonitor monitor = playerInstance.getGlobal(EvolutionGraphBuilder.PLAY_AREA);
		PlayersScoreBoard scoreBoards = playerInstance.getGlobal(EvolutionGraphBuilder.SCOREBOARDS);
		FeedingActionSummaries actions = playerInstance.getLocal(EvolutionGraphBuilder.FEEDING_ACTIONS);
		int draw = monitor.getDraw();

		List<RenderedEvent> rendered = new ArrayList<>(renderAll(player, events));

		Map<Integer, PlayerStateChanged> states = new HashMap<>();
		for (Event e : events) {
			if (e instanceof PlayerStateChanged) {
				PlayerStateChanged psc = (PlayerStateChanged) e;
				states.put(psc.getPlayer(), psc);
			}
		}

		for (PlayerStateChanged psc : states.values()) {
			rendered.add(render(psc));
		}

		return new PartialRender(draw, actions(player, actions), scoreBoards != null ? scoreBoards.getBoards() : null,
				rendered);
	}

	List<RenderedEvent> renderAll(int player, List<Event> events) {
		return events.stream().flatMap(e -> Optional.ofNullable(render(player, e)).stream())
				.collect(Collectors.toList());
	}

	private ActionsView actions(int player, FeedingActionSummaries actions) {
		if (actions != null) {
			List<SummaryView> collect = actions.stream().map(a -> renderAction(player, a)).collect(Collectors.toList());
			boolean pass = actions.isPass();
			return new ActionsView(collect, pass);

		}
		return null;
	}

	SummaryView renderAction(int player, FeedingActionSummary in) {
		if (in instanceof FeedSummary) {
			FeedSummary feed = (FeedSummary) in;
			return new FeedSumaryView(feed.getSpecie(), renderAll(player, feed.getEvents()));
		}

		else if (in instanceof IntelligentFeedSummary) {
			IntelligentFeedSummary feed = (IntelligentFeedSummary) in;
			return new IntelligentFeedSumaryView(feed.getSpecie(), feed.getCard(), new UsedTraitView(feed.getTrait()),
					renderAll(player, feed.getEvents()));
		} else if (in instanceof AttackSummary) {
			AttackSummary attack = (AttackSummary) in;

			return new AttackSummaryView(attack.getSpecie(), attack.getTarget(),
					attack.getViolations().stream().map(AttackViolationView::new).collect(Collectors.toList()),
					attack.getOutcomes().stream().map(o -> attackView(player, o)).collect(Collectors.toList()));
		}

		return null;
	}

	private AttackOutcomeView attackView(int player, AttackOutcome out) {
		return new AttackOutcomeView(out.getDisabled(), out.getCost(), renderAll(player, out.getEvents()));
	}

	private RenderedEvent render(int player, Event event) {
		if (event instanceof NextStepEvent) {
			return render((NextStepEvent) event);
		} else if (event instanceof NewTurnEvent) {
			return render((NewTurnEvent) event);
		} else if (event instanceof PlayerPassedEvent) {
			return render((PlayerPassedEvent) event);
		} else if (event instanceof CardDealed) {
			return render(player, (CardDealed) event);
		} else if (event instanceof FoodEaten) {
			return render((FoodEaten) event);
		} else if (event instanceof FatMoved) {
			return render((FatMoved) event);
		} else if (event instanceof CardAddedToPool) {
			return render(player, (CardAddedToPool) event);
		} else if (event instanceof DiscardPoolFood) {
			return render((DiscardPoolFood) event);
		} else if (event instanceof TraitAdded) {
			return render(player, (TraitAdded) event);
		} else if (event instanceof PoolRevealed) {
			return render((PoolRevealed) event);
		} else if (event instanceof TraitsRevealed) {
			return render((TraitsRevealed) event);
		} else if (event instanceof FoodScored) {
			return render((FoodScored) event);
		} else if (event instanceof PopulationGrow) {
			return render((PopulationGrow) event);
		} else if (event instanceof PopulationReduced) {
			return render((PopulationReduced) event);
		} else if (event instanceof PopulationIncreased) {
			return render((PopulationIncreased) event);
		} else if (event instanceof SizeIncreased) {
			return render((SizeIncreased) event);
		} else if (event instanceof SpecieAdded) {
			return render((SpecieAdded) event);
		} else if (event instanceof Attacked) {
			return render((Attacked) event);
		} else if (event instanceof SpecieExtincted) {
			return render((SpecieExtincted) event);
		}

		else if (event instanceof LastTurnEvent) {
			return new RenderedEvent("last-turn");
		}

		return null;
	}

	private RenderedEvent render(Attacked attacked) {
		return new RenderedEvent("specie-attacked")
				.put("specie", attacked.getSrc())
				.put("attacker", attacked.getAttacker())
				.put("disabled",
						attacked.getDisabled().stream().map(DisabledViolationView::new).collect(Collectors.toList()));
	}

	private RenderedEvent render(TraitsRevealed revealed) {

		Map<Integer, CardView> traits = new HashMap<>();
		for (Entry<Integer, Card> e : revealed.getTraits().entrySet()) {
			traits.put(e.getKey(), new CardView(e.getValue()));
		}

		RenderedEvent evt = new RenderedEvent("traits-revealed")
				.put("specie", revealed.getSrc())
				.put("traits", traits);

		return evt;
	}

	private RenderedEvent render(PlayerPassedEvent pass) {
		return new RenderedEvent("player-passed")
				.put("player", pass.getPlayer());
	}

	private RenderedEvent render(FoodScored score) {
		return new RenderedEvent("specie-food-scored")
				.put("specie", score.getSrc())
				.put("player", score.getPlayer())
				.put("score", score.getScore());
	}

	private RenderedEvent render(PopulationGrow grow) {
		return new RenderedEvent("specie-population-growed")
				.put("specie", grow.getSrc())
				.put("population", grow.getTo())
				.put("trait", new UsedTraitView(grow.getTrait()));
	}

	private RenderedEvent render(PopulationReduced reduced) {
		RenderedEvent re = new RenderedEvent("specie-population-reduced")
				.put("specie", reduced.getSrc())
				.put("population", reduced.getTo());

		if (reduced.getTrait() != null)
			re.put("trait", new UsedTraitView(reduced.getTrait()));
		return re;
	}

	private RenderedEvent render(SpecieExtincted extincted) {
		return new RenderedEvent("specie-extincted")
				.put("specie", extincted.getSrc());
	}

	private RenderedEvent render(PopulationIncreased increased) {
		return new RenderedEvent("specie-population-increased")
				.put("player", increased.getPlayer())
				.put("specie", increased.getSrc())
				.put("population", increased.getTo())
				.put("discarded", new CardView(increased.getCard()));
	}

	private RenderedEvent render(SizeIncreased increased) {
		return new RenderedEvent("specie-size-increased")
				.put("player", increased.getPlayer())
				.put("specie", increased.getSrc())
				.put("size", increased.getTo())
				.put("discarded", new CardView(increased.getCard()));
	}

	private RenderedEvent render(SpecieAdded added) {
		RenderedEvent evt = new RenderedEvent("specie-added")
				.put("player", added.getPlayer())
				.put("specie", added.getSrc())
				.put("position", added.getPosition());
		Card card = added.getCard();
		if (card != null)
			evt.put("discarded", new CardView(card));
		return evt;
	}

	private RenderedEvent render(FoodEaten foodEaten) {
		FoodConsumption consumption = foodEaten.getConsumption();
		FoodSource source = foodEaten.getSource();
		SpecieId specie = foodEaten.getSrc();
		RenderedEvent evt = new RenderedEvent("specie-food-eaten")
				.put("player", specie.getPlayer())
				.put("specie", specie)
				.put("food", consumption.getFood())
				.put("fat", consumption.getFat())
				.put("source", source);

		if (FoodSource.POOL == source)
			evt.put("delta", -foodEaten.getConsumed());
		List<UsedTrait> traits = foodEaten.getTraits();
		if (!traits.isEmpty())
			evt.put("traits", traits.stream().map(UsedTraitView::new).collect(Collectors.toList()));

		Card discarded = foodEaten.getDiscardedCard();
		if (discarded != null)
			evt.put("discarded", new CardView(discarded));
		return evt;
	}

	private RenderedEvent render(FatMoved fatMoved) {
		return new RenderedEvent("specie-fat-moved")
				.put("specie", fatMoved.getSrc())
				.put("fat", fatMoved.getFat());
	}

	private RenderedEvent render(NextStepEvent nextStepEvent) {
		return new RenderedEvent("new-step")
				.put("step", nextStepEvent.getStep());
	}

	private RenderedEvent render(NewTurnEvent newTurnEvent) {
		return new RenderedEvent("new-turn")
				.put("first", newTurnEvent.getFirstPlayer());
	}

	private RenderedEvent render(PlayerStateChanged state) {
		return new RenderedEvent("player-state-changed")
				.put("player", state.getPlayer())
				.put("state", state.getState());
	}

	private RenderedEvent render(PoolRevealed revealed) {
		return new RenderedEvent("pool-revealed")
				.put("delta", revealed.getDelta())
				.put("cards", revealed.getCards().stream().map(CardView::new).collect(Collectors.toList()));
	}

	private RenderedEvent render(DiscardPoolFood discard) {
		return new RenderedEvent("pool-discarded")
				.put("food", discard.getFood())
				.put("delta", discard.getDelta())
				.put("trait", new UsedTraitView(discard.getTrait()));
	}

	private RenderedEvent render(int current, CardDealed cardDealed) {
		List<Card> cards = cardDealed.getCards();
		int player = cardDealed.getPlayer();
		RenderedEvent out = new RenderedEvent("player-card-dealed")
				.put("player", player)
				.put("count", cards.size())
				.put("shuffle", cardDealed.isShuffle());

		if (player == current)
			out.put("cards", cards.stream().map(CardView::new).collect(Collectors.toList()));

		return out;
	}

	private RenderedEvent render(int current, CardAddedToPool added) {
		int player = added.getPlayer();
		RenderedEvent out = new RenderedEvent("player-card-added-to-pool")
				.put("player", player);

		if (player == current)
			out.put("card", new CardView(added.getCard()));

		return out;
	}

	private RenderedEvent render(int current, TraitAdded added) {
		int player = added.getPlayer();
		SpecieId specie = added.getSrc();
		RenderedEvent out = new RenderedEvent("specie-trait-added")
				.put("player", specie.getPlayer())
				.put("specie", specie)
				.put("index", added.getIndex());

		if (player == current)
			out.put("card", new CardView(added.getCard()));

		return out;
	}

}
