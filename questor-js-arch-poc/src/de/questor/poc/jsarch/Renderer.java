package de.questor.poc.jsarch;

import android.content.Context;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class Renderer {
	
	public static Renderer INSTANCE;
	
	private static final String TAG = "Renderer";

	private static final int SHOW_STORY_NODE_REQUEST = 0;

	Context mContext;
	WebView mWebView;
	
	MessageService messageService;
	
	QuestorContext questorContext;

	public Renderer(Context pContext) {
		INSTANCE = this;

		mContext = pContext;
		mWebView = new WebView(mContext);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.addJavascriptInterface(new RendererRuntime(mContext), "runtime");
		mWebView.addJavascriptInterface(new Logger("Renderer"), "logger");
		
		mWebView.loadUrl("file:///android_asset/renderer/renderer.html");
	}
	
	public void stationOnSubmit(String value) {
		mWebView.loadUrl(String.format("javascript:(function() { station.answer = '%s'; station.onSubmit() })()", value));
	}

	public void onMessage(String type, QuestorContext ctx, String msg) {
		if ("create".equals(type)) {
			// TODO: Destroy old questor context if it exists
			questorContext = ctx;

			// Runs the creation command.
			String command = String.format("javascript:(function() { %s })()", msg);
			Log.i(TAG, "creation: " + command);
			mWebView.loadUrl(command);
		} else {
			Log.w(TAG, "Unexpected message type: " + type);
		}
	}

	
	/*
	public class JavaScriptRuntimeBridge {

		public JavaScriptRuntimeBridge() {
		}

		public void showToast(String toast) {
			Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();

		}

		public void showQuizStation(String pQuestion) {
			Intent i = new Intent(mContext, QuizActivity.class);
			i.putExtra("question", pQuestion);
			//((Activity)mContext).startActivityForResult(i, SHOW_STORY_NODE_REQUEST);
			mContext.startActivity(i);
		}
		
		public void showHtmlStation(String pContent) {
			Log.i(TAG, "showHTMLStation");

			Intent i = new Intent(mContext, HtmlActivity.class);
			i.putExtra("content", pContent);
			mContext.startActivity(i);
		}
		
		public void sendReply(String msg) {
			Log.i(TAG, "reply: " + msg);
			questorContext.sendMessage(msg);
		}
		
		
	}
	*/

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

	public void joinTest() {
		// Lets the player join a game and thereby start the whole interaction
		messageService.sendToSimulator("join", null, "testspieler");
	}

}
