package fr.keyser.fsm.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import fr.keyser.fsm.AutomatEvent;
import fr.keyser.fsm.AutomatInstanceContainer;
import fr.keyser.fsm.AutomatLifeCycle;
import fr.keyser.fsm.AutomatLifeCycleEvent;
import fr.keyser.fsm.AutomatLogic;
import fr.keyser.fsm.InstanceId;

/**
 * An automat runner. The logic is stored in an {@link AutomatLogic} and the
 * data in a {@link DefaultAutomatInstanceContainer}. The inner
 * {@link DefaultAutomatInstanceContainer} is mutable, so each call of
 * {@link #get()} may supply a different insntance
 * 
 * @author pakeyser
 *
 */
public class AutomatEngine implements Supplier<AutomatInstanceContainer> {

	private static class AutomatEventDispatch {
		private final AutomatEvent event;
		private final Collection<InstanceId> ids;

		public AutomatEventDispatch(Collection<InstanceId> ids, AutomatEvent event) {
			this.ids = ids;
			this.event = event;
		}

		public AutomatEvent getEvent() {
			return event;
		}

		public Collection<InstanceId> getIds() {
			return ids;
		}
	}

	/**
	 * An empty container with a single {@link AutomatInstanceValue} called
	 * {@link AutomatLifeCycle#ROOT}
	 */
	private final static AutomatInstanceContainerValue EMPTY = AutomatInstanceContainerValue.create(
			Collections.singletonList(AutomatInstanceValue.create(AutomatLifeCycle.ROOT, AutomatLifeCycle.INITIAL, null,
					0, Collections.emptyList(), Collections.emptyMap())),
			Collections.emptyMap());

	public static AutomatEngine start(AutomatLogic logic, Object payload) {
		AutomatEngine engine = new AutomatEngine(EMPTY, logic);

		engine.get().getRoot().unicast(AutomatLifeCycleEvent.START.event(payload));

		return engine;
	}

	private DefaultAutomatInstanceContainer container;

	private final MultiPriorityQueue<AutomatEventDispatch> events = new MultiPriorityQueue<>();

	private final AtomicInteger worker = new AtomicInteger(0);

	public AutomatEngine(AutomatInstanceContainerValue value, AutomatLogic logic) {
		container = new DefaultAutomatInstanceContainer(value, this, logic);
	}
	
	public void start(Object payload) {
		get().getRoot().unicast(AutomatLifeCycleEvent.START.event(payload));
	}

	@Override
	public AutomatInstanceContainer get() {
		return container;
	}

	public void multicast(int priority, Collection<InstanceId> ids, AutomatEvent event) {
		events.add(priority, new AutomatEventDispatch(ids, event));

		if (worker.getAndIncrement() == 0) {
			DefaultAutomatInstanceContainer container = this.container;
			while (!events.isEmpty()) {
				try {
					AutomatEventDispatch task = events.remove();

					for (InstanceId id : task.getIds())
						container = container.process(id, task.getEvent());

				} finally {
					worker.getAndDecrement();
				}

			}
			// update the container only it there is no exception
			this.container = container;
		}
	}

	public AutomatInstanceContainerValue getInternal() {
		return container.getInternal();
	}
}
