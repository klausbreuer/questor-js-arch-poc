package de.questor.poc.jsarch.simulator;

import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import de.questor.poc.jsarch.Logger;
import de.questor.poc.jsarch.MessageService;
import de.questor.poc.jsarch.Renderer;

/*
 * The Simulator's runtime.
 * 
 * This class represents the environment for the Javascript-based core.
 * 
 */
public class SimulatorRuntime {
	
	MessageService messageService;
	
	private WebView wv;
	
	private Runnable runnable;
	
	public SimulatorRuntime(Context ctx) {
		wv = new WebView(ctx);

		wv.getSettings().setJavaScriptEnabled(true);
		wv.setWebChromeClient(new WebChromeClient());
		wv.addJavascriptInterface(new Logger("Simulator"), "logger");
		wv.addJavascriptInterface(this, "runtime");

		wv.loadUrl("file:///android_asset/simulator/simulator.html");
		
		// Initializes the global simulator instance in Javascript.
		runnable = new Runnable() {
			public void run() {
				wv.loadUrl("javascript:(function() { simulator = new Simulator(); })()");
			}
		};

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
	 * <p>The <em>key</em> for the <code>contextKey</code> argument has to be generated
	 * using the Javascript method <code>Simulator.toKey(Session)</code>. In the Java code
	 * we are not interested what the actual value is. We only guarantee that upon a reply
	 * from the {@link Renderer} we will use the same value.</p>
	 * 
	 * @param type
	 * @param contextKey
	 * @param msg
	 */
	public void sendToRenderer(String type, String contextKey, String msg) {
		messageService.sendToRenderer(type, (Object) contextKey, msg);
	}
	
	/** This method is being called by the {@link MessageService} each time there is a
	 * new message available.
	 * 
	 * @param type
	 * @param ctx
	 * @param msg
	 */
	public void onMessage(String type, Object ctx, String msg) {
		wv.loadUrl(String.format("javascript:simulator.onMessage('%s', '%s', '%s')", type, (String) ctx, msg));
	}

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

}
