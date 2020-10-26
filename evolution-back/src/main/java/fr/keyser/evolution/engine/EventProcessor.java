package fr.keyser.evolution.engine;

import java.util.Arrays;
import java.util.List;

/**
 * A reactive EventProcessor
 * 
 * @author pakeyser
 *
 * @param <E>
 * @param <P>
 */
public interface EventProcessor<E extends Event, P extends EventProcessor<E, P>> {

	/**
	 * Process the event and sends the downstream event to the consumer
	 * 
	 * @param consumer
	 * @param event
	 * @return the new EventProcessor
	 */
	public P process(EventConsumer<E> consumer, E event);
	
	/**
	 * Process anand return the tuple that contains all the events
	 * generated and processed and the final state
	 * 
	 * @param event
	 * @return
	 */
	public default Events<E, P> process(E event) {		
		return process(Arrays.asList(event));
	}


	/**
	 * Process a list of event and return the tuple that contains all the events
	 * generated and processed and the final state
	 * 
	 * @param events
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public default Events<E, P> process(List<? extends E> events) {
		return new Events<>((P)this, events);
	}
}
