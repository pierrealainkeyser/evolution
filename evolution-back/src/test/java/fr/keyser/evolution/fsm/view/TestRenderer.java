package fr.keyser.evolution.fsm.view;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import fr.keyser.evolution.core.DeckBuilder;
import fr.keyser.evolution.engine.Event;
import fr.keyser.evolution.event.Attacked;
import fr.keyser.evolution.event.DiscardPoolFood;
import fr.keyser.evolution.event.FatMoved;
import fr.keyser.evolution.event.FoodEaten;
import fr.keyser.evolution.event.FoodScored;
import fr.keyser.evolution.event.PopulationGrow;
import fr.keyser.evolution.event.PopulationIncreased;
import fr.keyser.evolution.event.PopulationReduced;
import fr.keyser.evolution.event.SizeIncreased;
import fr.keyser.evolution.event.SpecieAdded;
import fr.keyser.evolution.model.DisabledViolation;
import fr.keyser.evolution.model.FoodConsumption;
import fr.keyser.evolution.model.FoodSource;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.model.SpeciePosition;
import fr.keyser.evolution.model.Trait;
import fr.keyser.evolution.model.UsedTrait;

public class TestRenderer {

	Renderer renderer = new Renderer();

	@Test
	void renderAll() {
		DeckBuilder builder = new DeckBuilder();

		SpecieId src = new SpecieId(0, 0);
		expect(new FoodEaten(src, new FoodConsumption(0, 1), FoodSource.PLANT,
				Arrays.asList(new UsedTrait(src, Trait.FAT_TISSUE)), null),
				specie("specie-food-eaten", src));

		expect(new FatMoved(src, 1),
				specie("specie-fat-moved", src));

		expect(new FoodScored(src, 1),
				specie("specie-food-scored", src));

		expect(new PopulationGrow(src, 1, new UsedTrait(src, Trait.FERTILE)),
				specie("specie-population-growed", src));

		expect(new PopulationIncreased(src, 1, builder.card(Trait.AMBUSH)),
				specie("specie-population-increased", src));

		expect(new PopulationReduced(src, 1, new UsedTrait(new SpecieId(1, 1), Trait.HORNS)),
				specie("specie-population-reduced", src));

		expect(new SizeIncreased(src, 1, builder.card(Trait.AMBUSH)),
				specie("specie-size-increased", src));

		expect(new SpecieAdded(src, SpeciePosition.LEFT, builder.card(Trait.AMBUSH)),
				specie("specie-added", src));

		expect(new Attacked(src, new SpecieId(1, 1),
				Arrays.asList(new DisabledViolation("size", null, new UsedTrait(src, Trait.PACK_HUNTING), null))),
				specie("specie-attacked", src));

		expect(new DiscardPoolFood(-1, 1, new UsedTrait(src, Trait.PEST)),
				re -> {
					assertThat(re.getType()).isEqualTo("pool-discarded");
				});

	}

	private Consumer<RenderedEvent> specie(String type, SpecieId src) {
		return re -> {
			assertThat(re.getType()).isEqualTo(type);
			assertThat(re.getObjets()).containsEntry("specie", src);
		};
	}

	private void expect(Event e, Consumer<RenderedEvent> check) {
		assertThat(renderer.renderAll(0, Arrays.asList(e)))
				.hasSize(1)
				.allSatisfy(check);
	}
}
