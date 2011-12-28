package de.questor.poc.jsarch;

import de.questor.poc.jsarch.simulator.SimulatorRuntime;

public class MessageService {
	
	private SimulatorRuntime simulator;
	
	private Renderer renderer;

	public MessageService(SimulatorRuntime s, Renderer r) {
		simulator = s;
		renderer = r;
		
		// Make message service globally available
		simulator.setMessageService(this);
		renderer.setMessageService(this);
	}
	
	public void sendToRenderer(String type, Object contextKey, String msg) {
		QuestorContext ctx = new AnswerContext(contextKey);
		renderer.onMessage(type, ctx, msg);
	}
	
	public void sendToSimulator(String type, Object contextKey, String msg) {
		simulator.onMessage(type, contextKey, msg);
	}
	
	private class AnswerContext implements QuestorContext {
		
		private Object contextKey;
		
		AnswerContext(Object contextKey) {
			this.contextKey = contextKey;
		}

		@Override
		public void sendMessage(String msg) {
			MessageService.this.sendToSimulator("reply", contextKey, msg);
		}
		
	}
}
