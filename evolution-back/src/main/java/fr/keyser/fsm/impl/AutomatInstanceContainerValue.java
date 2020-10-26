package fr.keyser.fsm.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.keyser.fsm.InstanceId;

/**
 * An imutable container of {@link AutomatInstanceValue} and a set of immutable
 * data
 * 
 * @author pakeyser
 *
 */
public class AutomatInstanceContainerValue {

	private final List<AutomatInstanceValue> all;

	private final Map<String, Object> data;

	public static AutomatInstanceContainerValue create(List<AutomatInstanceValue> all, Map<String, Object> data) {
		return new AutomatInstanceContainerValue(Collections.unmodifiableList(all), Collections.unmodifiableMap(data));

	}

	private AutomatInstanceContainerValue(List<AutomatInstanceValue> all, Map<String, Object> data) {
		this.all = all;
		this.data = data;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public List<AutomatInstanceValue> getAll() {
		return all;
	}

	public AutomatInstanceValue getInstance(InstanceId id) {
		return getAll().stream().filter(ai -> ai.getId().equals(id)).findFirst().orElse(null);
	}

	public AutomatInstanceContainerValue withData(String key, Object value) {
		Map<String, Object> data = new HashMap<>(this.data);
		if (value == null)
			data.remove(key);
		else
			data.put(key, value);
		return new AutomatInstanceContainerValue(all, Collections.unmodifiableMap(data));
	}

	public AutomatInstanceContainerValue updateInstance(InstanceId id, UnaryOperator<AutomatInstanceValue> updater) {
		return new AutomatInstanceContainerValue(Collections.unmodifiableList(all.stream().map(ai -> {
			if (ai.getId().equals(id)) {
				return updater.apply(ai);
			} else {
				return ai;
			}
		}).collect(Collectors.toList())), data);
	}

	public AutomatInstanceContainerValue removeInstance(InstanceId id) {
		return new AutomatInstanceContainerValue(Collections.unmodifiableList(all.stream().flatMap(ai -> {
			if (ai.getId().equals(id)) {
				return Stream.empty();
			} else {
				return Stream.of(ai);
			}
		}).collect(Collectors.toList())), data);
	}

	public AutomatInstanceContainerValue startAll(List<AutomatInstanceValue> automatInstances) {

		List<AutomatInstanceValue> newAll = new ArrayList<>(all.size() + automatInstances.size());
		newAll.addAll(all);
		newAll.addAll(automatInstances);

		return new AutomatInstanceContainerValue(Collections.unmodifiableList(newAll), data);
	}

}
