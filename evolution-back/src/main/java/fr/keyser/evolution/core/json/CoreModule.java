package fr.keyser.evolution.core.json;

import com.fasterxml.jackson.databind.module.SimpleModule;

import fr.keyser.evolution.core.PlayArea;
import fr.keyser.evolution.fsm.PlayAreaMonitor;

public class CoreModule extends SimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3367653242343128502L;

	public CoreModule() {
		super("evolution-core");
		addDeserializer(PlayArea.class, new PlayAreaDeserializer());
		addDeserializer(PlayAreaMonitor.class, new PlayAreaMonitorDeserializer());

	}
}
