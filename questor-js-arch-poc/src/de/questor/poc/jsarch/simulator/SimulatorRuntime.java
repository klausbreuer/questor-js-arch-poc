package de.questor.poc.jsarch.simulator;

import java.io.IOException;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import de.questor.poc.jsarch.Interpreter;
import de.questor.poc.jsarch.Logger;
import de.questor.poc.jsarch.MessageService;

/*
 * The Simulator's runtime.
 * 
 * This class represents the environment for the Javascript-based core.
 * 
 */
public class SimulatorRuntime {
	
	MessageService messageService;
	
	private Interpreter interpreter;
	
	private Runnable runnable;

	private String invalidationMessage;
	
	public SimulatorRuntime(final Context ctx) {
		WebView wv = new WebView(ctx);
		interpreter = new Interpreter(wv);

		wv.getSettings().setJavaScriptEnabled(true);
		wv.setWebChromeClient(new WebChromeClient());
		wv.addJavascriptInterface(new Logger("Simulator"), "logger");
		wv.addJavascriptInterface(this, "runtime");

		// Initializes the global simulator instance in Javascript.
		runnable = new Runnable() {
			public void run() {
				interpreter.eval("simulator = new Simulator('Android PoC - Internal Simulator');");
				
				try {
					interpreter.eval(ctx.getAssets().open("simulator/game1.js"));
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
				
				// Makes sure that everything has been initialized correctly.
				interpreter.eval("checkSimulator();");
			}
		};

		wv.loadUrl("file:///android_asset/simulator/simulator.html");
	}

	/**
	 * This method is called once at the end of the parsing step of simulator.html
	 * (respectively simulator.js).
	 */
	public void finished() {
		runnable.run();
		runnable = null;
	}
	
	/**
	 * This method can be called from the Javascript environment to denote the end of the
	 * simulation.
	 * 
	 * @param i
	 */
	public void exit(int i) {
		System.exit(i);
	}
	
	/** This method is available to the Javascript environment in order to send
	 * a message to the renderer.
	 * 
O	 * @param type
	 * @param sessionId
	 * @param msg
	 */
	public void sendToRenderer(String sessionId, String msg) {
		if (messageService != null)
			messageService.sendToRenderer(sessionId, msg);
	}
	
	public void setInvalidationMessage(String invalidationMessage) {
		this.invalidationMessage = invalidationMessage;
	}
	
	/** This method is being called by the {@link MessageService} each time there is a
	 * new message available.
	 * 
	 * @param type
	 * @param sessionId
	 * @param msg
	 */
	public void onMessage(String sessionId, String msg) {
		if (msg == null) {
			msg = invalidationMessage;
		} else if (msg.contains("'")) {
			// msg is not supposed to contain ' (single quote) chars otherwise
			// the call is not going to work (yet).
			Log.e("Simulator", "Unable to process message: " + msg);
			throw new IllegalStateException("Message contains single-quotes. You need to fix that!");
		}
		
		interpreter.eval(String.format("simulator.onMessage('%s', '%s');", sessionId, msg));
	}

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

}
