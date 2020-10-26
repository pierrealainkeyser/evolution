package fr.keyser.fsm;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public interface AutomatInstance {

	AutomatInstanceContainer getContainer();

	InstanceId getId();

	State getCurrent();

	Optional<AutomatInstance> getParent();

	List<InstanceId> getChildsId();

	default List<AutomatInstance> getChilds() {
		AutomatInstanceContainer container = getContainer();
		return getChildsId().stream().map(container::getInstance).collect(Collectors.toList());
	}

	int getIndex();

	<T> T getLocal(String key);

	<T> T getGlobal(String key);

	// ------------------------------

	default AutomatInstance unicast(Supplier<AutomatEvent> event) {
		return unicast(event.get());
	}

	default AutomatInstance unicast(AutomatEvent event) {
		return multicast(Collections.singletonList(getId()), event);
	}

	default AutomatInstance broadcast(Supplier<AutomatEvent> event) {
		return broadcast(event.get());
	}

	default AutomatInstance broadcast(AutomatEvent event) {
		return multicast(getContainer().getAll().stream().map(AutomatInstance::getId).collect(Collectors.toList()),
				event);
	}

	default AutomatInstance multicast(Collection<InstanceId> ids, Supplier<AutomatEvent> event) {
		return multicast(ids, event.get());
	}

	default AutomatInstance multicast(Collection<InstanceId> ids, AutomatEvent event) {
		getContainer().multicast(ids, event);
		return this;
	}

	// ------------------------------

	AutomatInstance setLocal(String key, Object value);

	default <T> AutomatInstance updateLocal(String key, UnaryOperator<T> operator) {
		T var = getLocal(key);
		T updated = operator.apply(var);
		return setLocal(key, updated);
	}

	AutomatInstance setGlobal(String key, Object value);

	default <T> AutomatInstance updateGlobal(String key, UnaryOperator<T> operator) {
		T var = getGlobal(key);
		T updated = operator.apply(var);
		return setGlobal(key, updated);
	}

	// ------------------------------

	AutomatInstance startChilds(int number, State start);

	AutomatInstance startChilds(List<InstanceId> ids, State start);
}
