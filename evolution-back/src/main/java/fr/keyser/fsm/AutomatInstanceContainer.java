package fr.keyser.fsm;

import java.util.Collection;
import java.util.List;

public interface AutomatInstanceContainer {

	List<AutomatInstance> getAll();

	void multicast(Collection<InstanceId> ids, AutomatEvent event);

	default AutomatInstance getInstance(InstanceId id) {
		return getAll().stream().filter(ai -> ai.getId().equals(id)).findFirst().orElse(null);
	}

	default AutomatInstance getRoot() {
		return getAll().stream().findFirst().orElse(null);
	}
}
