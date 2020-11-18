package fr.keyser.evolution.web;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;

import fr.keyser.evolution.command.AddCardToPoolCommand;
import fr.keyser.evolution.command.AddSpeciesCommand;
import fr.keyser.evolution.command.AddTraitCommand;
import fr.keyser.evolution.command.AttackCommand;
import fr.keyser.evolution.command.FeedCommand;
import fr.keyser.evolution.command.IncreasePopulationCommand;
import fr.keyser.evolution.command.IncreaseSizeCommand;
import fr.keyser.evolution.command.IntelligentFeedCommand;
import fr.keyser.evolution.fsm.BridgeService;
import fr.keyser.evolution.fsm.view.CompleteRender;

public class EvolutionGameController {

	private final BridgeService bridgeService;

	public EvolutionGameController(BridgeService bridgeService) {
		this.bridgeService = bridgeService;
	}

	@SubscribeMapping("/game/{uuid}")
	public CompleteRender connect(@DestinationVariable String uuid) {
		return bridgeService.connect(uuid);
	}

	/*------- select food -------- */

	@MessageMapping("/game/{uuid}/select-food")
	public void selectFood(@DestinationVariable String uuid, @Payload AddCardToPoolCommand cmd) {
		bridgeService.selectFood(uuid, cmd);
	}

	/*------- feeding -------- */

	@MessageMapping("/game/{uuid}/attack")
	public void attack(@DestinationVariable String uuid, @Payload AttackCommand cmd) {
		bridgeService.feed(uuid, cmd);
	}

	@MessageMapping("/game/{uuid}/feed")
	public void feed(@DestinationVariable String uuid, @Payload FeedCommand cmd) {
		bridgeService.feed(uuid, cmd);
	}

	@MessageMapping("/game/{uuid}/intelligent-feed")
	public void intelligentFeed(@DestinationVariable String uuid, @Payload IntelligentFeedCommand cmd) {
		bridgeService.feed(uuid, cmd);
	}
	/*------- playing cards  -------- */

	@MessageMapping("/game/{uuid}/add-specie")
	public void addSpecie(@DestinationVariable String uuid, @Payload AddSpeciesCommand cmd) {
		bridgeService.playCard(uuid, cmd);
	}

	@MessageMapping("/game/{uuid}/add-trait")
	public void addTrait(@DestinationVariable String uuid, @Payload AddTraitCommand cmd) {
		bridgeService.playCard(uuid, cmd);
	}

	@MessageMapping("/game/{uuid}/increase-population")
	public void increasePopulation(@DestinationVariable String uuid, @Payload IncreasePopulationCommand cmd) {
		bridgeService.playCard(uuid, cmd);
	}

	@MessageMapping("/game/{uuid}/increase-size")
	public void increaseSize(@DestinationVariable String uuid, @Payload IncreaseSizeCommand cmd) {
		bridgeService.playCard(uuid, cmd);
	}

	@MessageMapping("/game/{uuid}/pass")
	public void pass(@DestinationVariable String uuid) {
		bridgeService.pass(uuid);
	}
}
