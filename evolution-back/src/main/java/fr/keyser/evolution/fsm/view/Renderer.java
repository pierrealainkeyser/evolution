package fr.keyser.evolution.fsm.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import fr.keyser.evolution.engine.Event;
import fr.keyser.evolution.event.CardAddedToPool;
import fr.keyser.evolution.event.CardDealed;
import fr.keyser.evolution.event.FoodEaten;
import fr.keyser.evolution.event.FoodScored;
import fr.keyser.evolution.event.LastTurnEvent;
import fr.keyser.evolution.event.NewTurnEvent;
import fr.keyser.evolution.event.NextStepEvent;
import fr.keyser.evolution.event.PlayerPassedEvent;
import fr.keyser.evolution.event.PlayerStateChanged;
import fr.keyser.evolution.event.PoolRevealed;
import fr.keyser.evolution.event.TraitAdded;
import fr.keyser.evolution.event.TraitsRevealed;
import fr.keyser.evolution.fsm.EvolutionGraphBuilder;
import fr.keyser.evolution.fsm.PlayAreaMonitor;
import fr.keyser.evolution.fsm.PlayerRef;
import fr.keyser.evolution.model.Card;
import fr.keyser.evolution.model.FoodConsumption;
import fr.keyser.evolution.model.FoodSource;
import fr.keyser.evolution.model.PlayerScoreBoard;
import fr.keyser.evolution.model.UsedTrait;
import fr.keyser.evolution.summary.FeedSummary;
import fr.keyser.evolution.summary.Summary;
import fr.keyser.fsm.AutomatInstance;

public class Renderer {

	public CompleteRender complete(int player, List<PlayerRef> players, AutomatInstance playerInstance) {
		PlayAreaMonitor monitor = playerInstance.getGlobal(EvolutionGraphBuilder.PLAY_AREA);
		int draw = monitor.getDraw();

		return new CompleteRender(draw);
	}

	public PartialRender partial(int player, AutomatInstance playerInstance, List<Event> events) {
		PlayAreaMonitor monitor = playerInstance.getGlobal(EvolutionGraphBuilder.PLAY_AREA);
		List<PlayerScoreBoard> scoreBoards = playerInstance.getGlobal(EvolutionGraphBuilder.SCOREBOARDS);
		List<Summary> actions = playerInstance.getLocal(EvolutionGraphBuilder.SUMMARY);
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

		return new PartialRender(draw, actions(player, actions), scoreBoards, rendered);
	}

	public List<RenderedEvent> renderAll(int player, List<Event> events) {
		return events.stream().flatMap(e -> Optional.ofNullable(render(player, e)).stream())
				.collect(Collectors.toList());
	}

	private List<SummaryView> actions(int player, List<Summary> actions) {
		if (actions != null) {
			return actions.stream().map(a -> renderAction(player, a)).collect(Collectors.toList());

		}
		return null;
	}

	private SummaryView renderAction(int player, Summary in) {
		if (in instanceof FeedSummary) {
			FeedSummary feed = (FeedSummary) in;
			return new FeedSumaryView(feed.getSpecie(), renderAll(player, feed.getEvents()));
		}

		return null;
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
		} else if (event instanceof CardAddedToPool) {
			return render(player, (CardAddedToPool) event);
		} else if (event instanceof TraitAdded) {
			return render(player, (TraitAdded) event);
		} else if (event instanceof PoolRevealed) {
			return render((PoolRevealed) event);
		} else if (event instanceof TraitsRevealed) {
			return render((TraitsRevealed) event);
		} else if (event instanceof FoodScored) {
			return render((FoodScored) event);
		} else if (event instanceof LastTurnEvent) {
			return new RenderedEvent("last-turn");
		}

		return null;
	}

	private RenderedEvent render(TraitsRevealed revealed) {
		RenderedEvent evt = new RenderedEvent("traits-revealed")
				.put("specie", revealed.getSrc().toString());
		revealed.getTraits().entrySet().forEach(e -> evt.put("traits[" + e.getKey() + "]", new CardView(e.getValue())));

		return evt;
	}

	private RenderedEvent render(PlayerPassedEvent pass) {
		return new RenderedEvent("player-passed")
				.put("player", pass.getPlayer());
	}

	private RenderedEvent render(FoodScored score) {
		return new RenderedEvent("food-scored")
				.put("player", score.getPlayer())
				.put("score", score.getScore());
	}

	private RenderedEvent render(FoodEaten foodEaten) {
		FoodConsumption consumption = foodEaten.getConsumption();
		FoodSource source = foodEaten.getSource();
		RenderedEvent evt = new RenderedEvent("specie-food-eaten")
				.put("specie", foodEaten.getSrc().toString())
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
		RenderedEvent out = new RenderedEvent("specie-trait-added")
				.put("specie", added.getSrc().toString())
				.put("index", added.getIndex());

		if (player == current)
			out.put("card", new CardView(added.getCard()));

		return out;
	}

}
