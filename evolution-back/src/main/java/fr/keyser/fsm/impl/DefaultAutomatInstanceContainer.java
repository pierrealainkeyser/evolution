package fr.keyser.fsm.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import fr.keyser.fsm.AutomatEvent;
import fr.keyser.fsm.AutomatInstance;
import fr.keyser.fsm.AutomatInstanceContainer;
import fr.keyser.fsm.AutomatLifeCycleEvent;
import fr.keyser.fsm.AutomatLogic;
import fr.keyser.fsm.FollowedTransition;
import fr.keyser.fsm.InstanceId;
import fr.keyser.fsm.State;

public class DefaultAutomatInstanceContainer implements AutomatInstanceContainer {

	private class DefaultAutomatInstance implements AutomatInstance {

		private final AutomatInstanceValue lookup;

		public DefaultAutomatInstance(InstanceId id) {
			this.lookup = lookup(id);
		}

		@Override
		public List<InstanceId> getChildsId() {
			return lookup.getChildsId();
		}

		@Override
		public AutomatInstanceContainer getContainer() {
			return DefaultAutomatInstanceContainer.this;
		}

		@Override
		public State getCurrent() {
			return lookup.getCurrent();
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T getGlobal(String key) {
			return (T) container.getData().get(key);
		}

		@Override
		public InstanceId getId() {
			return lookup.getId();
		}

		@Override
		public int getIndex() {
			return lookup.getIndex();
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T getLocal(String key) {
			return (T) lookup.getData().get(key);
		}

		@Override
		public Optional<AutomatInstance> getParent() {
			InstanceId parentId = lookup.getParentId();
			if (parentId == null)
				return Optional.empty();

			return Optional.of(getInstance(parentId));

		}

		@Override
		public AutomatInstance setGlobal(String key, Object value) {
			InstanceId id = getId();
			DefaultAutomatInstanceContainer updated = update(container -> container.withData(key, value));
			return updated.getInstance(id);
		}

		@Override
		public AutomatInstance setLocal(String key, Object value) {
			InstanceId id = getId();
			DefaultAutomatInstanceContainer updated = updateInstance(id, ai -> ai.withData(key, value));
			return updated.getInstance(id);
		}

		@Override
		public AutomatInstance startChilds(int number, State from) {
			int size = all.size();
			List<InstanceId> childsIds = IntStream.range(size, size + number).mapToObj(i -> new InstanceId("" + i))
					.collect(Collectors.toList());
			return startChilds(childsIds, from);

		}

		@Override
		public AutomatInstance startChilds(List<InstanceId> childsIds, State from) {
			InstanceId id = getId();
			DefaultAutomatInstanceContainer updated = DefaultAutomatInstanceContainer.this.startChilds(id, childsIds,
					from);
			return updated.getInstance(id);
		}
	}

	private final List<AutomatInstance> all;

	private final AutomatInstanceContainerValue container;

	private final AutomatEngine engine;

	private final AutomatLogic logic;

	DefaultAutomatInstanceContainer(AutomatInstanceContainerValue container, AutomatEngine engine, AutomatLogic logic) {
		this.container = container;
		this.engine = engine;
		this.logic = logic;
		this.all = Collections.unmodifiableList(container.getAll().stream()
				.map(ai -> new DefaultAutomatInstance(ai.getId())).collect(Collectors.toList()));
	}

	@Override
	public List<AutomatInstance> getAll() {
		return all;
	}

	public AutomatInstanceContainerValue getInternal() {
		return container;
	}

	private AutomatInstanceValue lookup(InstanceId id) {
		return container.getInstance(id);
	}

	@Override
	public void multicast(Collection<InstanceId> ids, AutomatEvent event) {
		engine.multicast(0, ids, event);
	}

	public List<InstanceId> getAllIds() {
		return container.getAll().stream().map(AutomatInstanceValue::getId).collect(Collectors.toList());
	}

	public DefaultAutomatInstanceContainer process(InstanceId id, AutomatEvent event) {

		AutomatInstance instance = getInstance(id);

		FollowedTransition transition = logic.findTransition(instance, event);
		if (transition == null)
			return this;

		Iterator<State> leaving = transition.leaving().iterator();
		while (leaving.hasNext())
			instance = logic.leaving(instance, leaving.next(), transition);

		Iterator<State> entering = transition.entering().iterator();
		while (entering.hasNext())
			instance = logic.entering(instance, entering.next(), transition);

		DefaultAutomatInstanceContainer container = (DefaultAutomatInstanceContainer) instance.getContainer();
		State to = transition.getTo();

		return container.updateInstance(id, ai -> ai.withState(to));
	}

	private DefaultAutomatInstanceContainer startChilds(InstanceId parentId, List<InstanceId> childsIds, State from) {
		int nb = childsIds.size();
		List<AutomatInstanceValue> newChilds = new ArrayList<>(nb);

		for (int i = 0; i < nb; ++i) {
			AutomatInstanceValue child = AutomatInstanceValue.create(childsIds.get(i), from, parentId, i,
					Collections.emptyList(), Collections.emptyMap());
			newChilds.add(child);

		}

		// create the new automats
		DefaultAutomatInstanceContainer updated = update(
				container -> container.updateInstance(parentId, old -> old.addChilds(childsIds)).startAll(newChilds));

		// multicast the START event
		updated.multicast(childsIds, AutomatLifeCycleEvent.START.get());

		// unicast the CHILDS_STARTED event to the parent
		updated.getInstance(parentId).unicast(AutomatLifeCycleEvent.CHILDS_STARTED);
		return updated;

	}

	private DefaultAutomatInstanceContainer update(UnaryOperator<AutomatInstanceContainerValue> operator) {
		return new DefaultAutomatInstanceContainer(operator.apply(container), engine, logic);
	}

	private DefaultAutomatInstanceContainer updateInstance(InstanceId id,
			UnaryOperator<AutomatInstanceValue> operator) {
		return update(container -> container.updateInstance(id, operator));
	}
}
