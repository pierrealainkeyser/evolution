package fr.keyser.fsm.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.keyser.fsm.InstanceId;
import fr.keyser.fsm.State;

/**
 * A immutable execution of an automat instance. With a set of local data
 * 
 * @author pakeyser
 *
 */
public class AutomatInstanceValue {

	private final InstanceId id;

	private final State current;

	private final InstanceId parentId;

	private final int index;

	private final List<InstanceId> childsId;

	private final Map<String, Object> data;

	public static AutomatInstanceValue create(InstanceId id, State current, InstanceId parentId, int index,
			List<InstanceId> childsId, Map<String, Object> data) {
		return new AutomatInstanceValue(id, current, parentId, index, Collections.unmodifiableList(childsId),
				Collections.unmodifiableMap(data));

	}

	private AutomatInstanceValue(InstanceId id, State current, InstanceId parentId, int index,
			List<InstanceId> childsId, Map<String, Object> data) {
		this.id = id;
		this.current = current;
		this.index = index;
		this.parentId = parentId;
		this.childsId = childsId;
		this.data = data;
	}

	public AutomatInstanceValue addChilds(List<InstanceId> childsId) {

		List<InstanceId> newChildsIds = new ArrayList<>(this.childsId.size() + childsId.size());
		newChildsIds.addAll(this.childsId);
		newChildsIds.addAll(childsId);

		return new AutomatInstanceValue(id, current, parentId, index, Collections.unmodifiableList(newChildsIds), data);
	}

	public AutomatInstanceValue withState(State current) {
		return new AutomatInstanceValue(id, current, parentId, index, childsId, data);
	}

	public int getIndex() {
		return index;
	}

	public AutomatInstanceValue withData(String key, Object value) {
		Map<String, Object> data = new HashMap<>(this.data);
		if (value == null)
			data.remove(key);
		else
			data.put(key, value);
		return new AutomatInstanceValue(id, current, parentId, index, childsId, Collections.unmodifiableMap(data));
	}

	public InstanceId getId() {
		return id;
	}

	public State getCurrent() {
		return current;
	}

	public InstanceId getParentId() {
		return parentId;
	}

	public List<InstanceId> getChildsId() {
		return childsId;
	}

	public Map<String, Object> getData() {
		return data;
	}

}
