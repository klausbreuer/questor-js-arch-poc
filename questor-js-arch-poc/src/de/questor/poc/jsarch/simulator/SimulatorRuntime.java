package de.questor.poc.jsarch.simulator;

import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import de.questor.poc.jsarch.Logger;
import de.questor.poc.jsarch.MessageService;

/*
 * The Simulator's runtime.
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
		
		// Initializes a simulator instance.
		runnable = new Runnable() {
			public void run() {
				wv.loadUrl("javascript:(function() { simulator = new Simulator(); })()");
			}
		};

	}

	public void finished() {
		runnable.run();
		runnable = null;
	}
	
	public void exit(int i) {
		System.exit(i);
	}
	
	public void sendToRenderer(String type, String contextKey, String msg) {
		messageService.sendToRenderer(type, (Object) contextKey, msg);
	}
	
	public void onMessage(String type, Object ctx, String msg) {
		wv.loadUrl(String.format("javascript:simulator.onMessage('%s', '%s', '%s')", type, (String) ctx, msg));
	}

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

}
