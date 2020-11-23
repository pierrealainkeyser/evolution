package fr.keyser.fsm.json;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.keyser.evolution.model.CardId;
import fr.keyser.evolution.model.SpecieId;
import fr.keyser.evolution.summary.FeedSummary;
import fr.keyser.evolution.summary.FeedingActionSummaries;
import fr.keyser.fsm.InstanceId;
import fr.keyser.fsm.State;
import fr.keyser.fsm.impl.AutomatInstanceContainerValue;
import fr.keyser.fsm.impl.AutomatInstanceValue;

public class TestJsonDataMapAdapter {

	private final static Logger logger = LoggerFactory.getLogger(TestJsonDataMapAdapter.class);

	@Test
	void automatInstanceValue() throws JsonProcessingException {

		JsonDataMapAdapter adapter = new JsonDataMapAdapter(Arrays.asList(InstanceId.class));

		AutomatInstanceValue in = AutomatInstanceValue.create(new InstanceId("root"), new State("yes", "no"), null, 1,
				Arrays.asList(new InstanceId("i1"), new InstanceId("i2")),
				Map.of("first", new InstanceId("i1"), "card", new CardId(56)));

		ObjectMapper om = new ObjectMapper().registerModule(adapter.asModule());

		String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(in);
		logger.info("in\n{}", json);

		AutomatInstanceValue out = om.readValue(json, AutomatInstanceValue.class);
		String jsonOut = om.writerWithDefaultPrettyPrinter().writeValueAsString(out);
		Assertions.assertThat(jsonOut).isEqualTo(json);
	}

	@Test
	void automatInstanceContainerValue() throws JsonProcessingException {

		JsonDataMapAdapter adapter = new JsonDataMapAdapter(
				Arrays.asList(InstanceId.class, FeedingActionSummaries.class));

		AutomatInstanceValue aiv = AutomatInstanceValue.create(new InstanceId("root"), new State("yes", "no"),
				new InstanceId("parent"), 1,
				Collections.emptyList(),
				Map.of("first", new InstanceId("i1")));

		AutomatInstanceContainerValue in = AutomatInstanceContainerValue.create(Arrays.asList(aiv),
				Map.of("actions", new FeedingActionSummaries(
						Arrays.asList(new FeedSummary(new SpecieId(0, 0), Arrays.asList())))));

		ObjectMapper om = new ObjectMapper().registerModule(adapter.asModule());

		String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(in);
		logger.info("in\n{}", json);

		AutomatInstanceContainerValue out = om.readValue(json, AutomatInstanceContainerValue.class);
		String jsonOut = om.writerWithDefaultPrettyPrinter().writeValueAsString(out);
		Assertions.assertThat(jsonOut).isEqualTo(json);
	}
}
