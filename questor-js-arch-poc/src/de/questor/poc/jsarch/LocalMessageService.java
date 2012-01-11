package de.questor.poc.jsarch;

import android.opengl.GLSurfaceView.Renderer;
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
	public void sendToRenderer(Object contextKey, String msg) {
		QuestorContext ctx = new AnswerContext(contextKey);
		renderer.onMessage(ctx, msg);
	}
	
	@Override
	public void sendToSimulator(Object contextKey, String msg) {
		simulator.onMessage(contextKey, msg);
	}
	
	/**
	 * By sending messages to the {@link Renderer} using this class the
	 * called service can reply without knowing the actual <code>contextKey</code>
	 * instance.
	 * 
	 * <p>In a real networked implementation this mechanism is also supposed to be
	 * implemented on the client-side of the network.</p>
	 * 
	 * @author Robert Schuster <r.schuster@tarent.de>
	 *
	 */
	private class AnswerContext implements QuestorContext {
		
		private Object contextKey;
		
		AnswerContext(Object contextKey) {
			this.contextKey = contextKey;
		}

		@Override
		public void sendMessage(String msg) {
			LocalMessageService.this.sendToSimulator(contextKey, msg);
		}
		
	}
}
