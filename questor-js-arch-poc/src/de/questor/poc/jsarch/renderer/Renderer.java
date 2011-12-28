package de.questor.poc.jsarch.renderer;

import android.content.Context;
import android.util.Log;
import android.webkit.WebChromeClient;
import de.questor.poc.jsarch.MessageService;
import de.questor.poc.jsarch.QWebView;
import de.questor.poc.jsarch.QuestorContext;

public class Renderer {

	public static Renderer INSTANCE;
	private static final String TAG = "Renderer";

	private Context mContext;
	private QWebView mWebView;
	
	private MessageService messageService;
	private QuestorContext questorContext;
	private RendererRuntime rendererRuntime;

	public Renderer(Context pContext) {
		INSTANCE = this;

		mContext = pContext;
		rendererRuntime = RendererRuntime.getInstance();
		rendererRuntime.setContext(mContext);
		
		mWebView = new QWebView(mContext);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.addJavascriptInterface(rendererRuntime, "runtime");
		mWebView.loadUrl("file:///android_asset/renderer/renderer.html");

	}
	

	public void onMessage(String type, QuestorContext ctx, String msg) {
		if ("create".equals(type)) {
			rendererRuntime.setQuestorContext(ctx);

			// Runs the creation command.
			String command = String.format("javascript:(function() { %s })()", msg);
			Log.i(TAG, "creation: " + command);
			mWebView.loadUrl(command);
		} else if ("poiPos".equals(type)) {
			// Klaus: hier kommt eine Positionsmeldung f�r ein POI der  Compass-Station an.
			// Eigentlich m�sste die jetzt an den js-core (renderer.js) weitergeleitet werden, der dann wiederum runtime.sendMessageToCompassStation aufrufen w�rde.
			// Dies js-Schleife spare ich mir jetzt mal und rufe direkt sendMessageToCompassStation auf.
			// (erst wenn die Compass-Logik in js realisiert ist, macht es wieder Sinn, diese Message an den js core weiter zu reichen... )
			rendererRuntime.sendMessageToCompassStation(type, msg);
		} else {
			Log.w(TAG, "Unexpected message type: " + type);
		}
	}

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

	public void joinTest() {
		// Lets the player join a game and thereby start the whole interaction
		messageService.sendToSimulator("join", null, "testspieler");
	}

}
