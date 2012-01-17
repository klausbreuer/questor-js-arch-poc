package de.questor.poc.jsarch;

import de.questor.poc.jsarch.renderer.RendererRuntime;
import de.questor.poc.jsarch.simulator.SimulatorRuntime;

/**
 * Implementation of the {@link MessageService} which uses a local
 * instance of the Simulator runtime and Renderer.
 * 
 * @author Robert Schuster <r.schuster@tarent.de>
 *
 */
public class LocalMessageService implements MessageService {
	
	private SimulatorRuntime simulator;
	
	private RendererRuntime renderer;

	public LocalMessageService(SimulatorRuntime s, RendererRuntime r) {
		simulator = s;
		renderer = r;
		
		// Make message service globally available
		simulator.setMessageService(this);
		renderer.setMessageService(this);
	}
	
	@Override
	public void sendToRenderer(String sessionId, String msg) {
		renderer.onMessage(sessionId, msg);
	}
	
	@Override
	public void sendToSimulator(String sessionId, String msg) {
		simulator.onMessage(sessionId, msg);
	}
	
}
