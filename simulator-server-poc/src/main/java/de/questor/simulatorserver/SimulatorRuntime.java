package de.questor.simulatorserver;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

/*
 * The Simulator's runtime.
 * 
 * This class represents the environment for the Javascript-based core.
 * 
 */
public class SimulatorRuntime {
	
	private boolean initialized = false;

	private Context context;

	private Scriptable scope;
	
	private MessageService messageService;

	private String invalidationMessage;
	
	public SimulatorRuntime(Context context, Scriptable scope) {
		this.context = context;
		this.scope = scope;
	}

	/**
	 * This method is called once at the end of the parsing step of simulator.html
	 * (respectively simulator.js).
	 */
	public void finished() {
		System.out.println("Parsing scripts finished.");
		initialized = true;
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	/**
	 * This method can be called from the Javascript environment to denote the end of the
	 * simulation.
	 * 
	 * @param i
	 */
	public void exit(int i) {
		System.out.println("Game script ended.");
		
		// Here we can shut down the engine.
	}
	
	/** This method is available to the Javascript environment in order to send
	 * a message to the renderer.
	 * 
	 * <p>The <em>key</em> for the <code>contextKey</code> argument has to be generated
	 * using the Javascript method <code>Simulator.toKey(Session)</code>. In the Java code
	 * we are not interested what the actual value is. We only guarantee that upon a reply
	 * from the <code>Renderer</code> we will use the same value.</p>
	 * 
	 * @param type
	 * @param contextKey
	 * @param msg
	 */
	public void sendToRenderer(String sessionId, String msg) {
		messageService.sendToRenderer(sessionId, msg);
	}
	
	public void setInvalidationMessage(String invalidationMessage) {
		this.invalidationMessage = invalidationMessage;
	}
	
	/** This method is being called by the {@link MessageService} each time there is a
	 * new message available.
	 * 
	 * @param type
	 * @param ctx
	 * @param msg
	 */
	public void onMessage(String sessionId, String msg) {
		Context context = ContextFactory.getGlobal().enterContext();
		
		if (msg == null) {
			msg = invalidationMessage;
		} else if (msg.contains("'")) {
			// msg is not supposed to contain ' (single quote) chars otherwise
			// the call is not going to work.
			throw new IllegalStateException("Message contains single-quotes. You need to fix that!");
		}
		
		String code = String.format("simulator.onMessage('%s', '%s')", sessionId, msg);
		context.evaluateString(scope, code, "custom", 0, null);
		
		Context.exit();
	}

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

}
