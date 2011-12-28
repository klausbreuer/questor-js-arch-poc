package de.questor.poc.jsarch.renderer;

import de.questor.poc.jsarch.MessageService;
import de.questor.poc.jsarch.QuestorContext;
import android.content.Context;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class Renderer {
	
	public static Renderer INSTANCE;
	private static final String TAG = "Renderer";

	private Context mContext;
	private WebView mWebView;
	
	private MessageService messageService;
	private QuestorContext questorContext;
	private RendererRuntime rendererRuntime;

	public Renderer(Context pContext) {
		INSTANCE = this;

		mContext = pContext;
		rendererRuntime = RendererRuntime.getInstance();
		rendererRuntime.setContext(mContext);
		
		mWebView = new WebView(mContext);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.addJavascriptInterface(rendererRuntime, "runtime");
		mWebView.loadUrl("file:///android_asset/renderer/renderer.html");
	}
	
	/*
	public void stationOnSubmit(String value) {
		mWebView.loadUrl(String.format("javascript:(function() { station.answer = '%s'; station.onSubmit() })()", value));
	}
	*/

	public void onMessage(String type, QuestorContext ctx, String msg) {
		if ("create".equals(type)) {
			rendererRuntime.setQuestorContext(ctx);

			// Runs the creation command.
			String command = String.format("javascript:(function() { %s })()", msg);
			Log.i(TAG, "creation: " + command);
			mWebView.loadUrl(command);
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
