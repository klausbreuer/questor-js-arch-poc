package de.questor.poc.jsarch.renderer;

import com.google.android.maps.GeoPoint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.widget.Toast;
import de.questor.poc.jsarch.Logger;
import de.questor.poc.jsarch.MessageService;
import de.questor.poc.jsarch.QWebView;
import de.questor.poc.jsarch.QuestorContext;

public class RendererRuntime {

	private static final String TAG = "Renderer";
	
	private static RendererRuntime INSTANCE;

	private Context mContext;
	private QWebView mWebView;

	private MessageService messageService;
	
	private QuestorContext mQuestorContext;
	
	private BroadcastReceiver br = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent i) {
			String msg = (String) i.getSerializableExtra("data");
			
			sendReply(msg);
		}
	}; 
	
	public RendererRuntime(Context pContext) {
		INSTANCE = this;
		mContext = pContext;

		mWebView = new QWebView(mContext);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.addJavascriptInterface(new Logger(TAG), "logger");
		mWebView.addJavascriptInterface(this, "runtime");
		mWebView.loadUrl("file:///android_asset/renderer/renderer.html");
	}
	
	public void onResume() {
		mContext.registerReceiver(br, new IntentFilter("de.questor.poc.jsarch.reply"));
	}
	
	public void onPause() {
		mContext.unregisterReceiver(br);
	}
	
	public static RendererRuntime getInstance() {
		if (INSTANCE == null) {
			throw new IllegalStateException("RendererRuntime not initialized yet.");
		}
		
		return INSTANCE;
	}
	
	public void onMessage(String type, QuestorContext ctx, String msg) {
		if ("create".equals(type)) {
			mQuestorContext = ctx;

			// Runs the creation command.
			String command = String.format("javascript:(function() { %s })()", msg);
			Log.i(TAG, "creation: " + command);
			mWebView.loadUrl(command);
		} else {
			// Assume message is for current station
			mWebView.loadUrl(String.format("javascript:(function() { station.onMessage('%s', '%s'); }) ()", type, msg)); 
		}
	}

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

	public void showToast(String toast) {
		Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
	}
	
	public void showQuizStation(String pQuestion) {
		Log.i(TAG, "showQuizStation");
		Intent i = new Intent(mContext, QuizActivity.class);
		i.putExtra("question", pQuestion);
		mContext.startActivity(i);
	}

	public void showHtmlStation(String pContent) {
		Log.i(TAG, "showHTMLStation");
		Intent i = new Intent(mContext, HtmlActivity.class);
		i.putExtra("content", pContent);
		mContext.startActivity(i);
	}

	public void showCompassStation() {
		Log.i(TAG, "showCompassStation");
		Intent i = new Intent(mContext, CompassActivity.class);
		mContext.startActivity(i);
	}

	public void sendMessageToCompassStation(String pType, String pMsg) {
		//Log.i(TAG, "sendMessageToCompassStation: " + pType + " / " + pMsg);		
		Intent i = new Intent("de.questor.poc.jsarch." + pType);
		i.putExtra(pType, pMsg);
		mContext.sendBroadcast(i);
	}
	
	/**
	 * A messaging function that is called from Javascript which allows sending
	 * a reply to the simulator.
	 * 
	 * @param msg
	 */
	public void sendReply(String msg) {
		Log.i(TAG, "reply: " + msg);
		if (mQuestorContext == null) {
			Log.i(TAG, "error: mQuestorContext is null!");
		}
		else {
			mQuestorContext.sendMessage(msg);
		}
	}

	public void finished() {
		Log.i(TAG, "finished loading renderer.html");
	}
}
