package de.questor.poc.jsarch.renderer;

import java.util.Iterator;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.widget.Toast;
import de.questor.poc.jsarch.Interpreter;
import de.questor.poc.jsarch.Logger;
import de.questor.poc.jsarch.MessageService;
import de.questor.poc.jsarch.QWebView;
import de.questor.poc.jsarch.QuestorContext;
import de.questor.poc.jsarch.renderer.compass.CompassActivity;
import de.questor.poc.jsarch.renderer.compass.CompassDelegate;

public class RendererRuntime {

	private static final String TAG = "Renderer";
	
	private static RendererRuntime INSTANCE;

	private Context mContext;
	private Interpreter interpreter;

	private MessageService messageService;
	
	private QuestorContext mQuestorContext;
	
	private LocationService locationService;
	
	private DelegateManager delegateManager;
	
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
		
		// Implementation note: This would be done elsewhere through a central
		// registry file or whatever
		delegateManager = new DelegateManager();
		delegateManager.registerDelegate("compassDelegate", new CompassDelegate(mContext));		
		// End of registration
		
		QWebView mWebView = new QWebView(mContext);
		interpreter = new Interpreter(mWebView);
		
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.addJavascriptInterface(new Logger(TAG), "logger");
		mWebView.addJavascriptInterface(this, "runtime");
		
		// delegate registration
		Iterator<Map.Entry<String, Object>> it = delegateManager.iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> e = it.next();
			mWebView.addJavascriptInterface(e.getValue(), e.getKey());
		}

		// Creates location service which can receive and distribute location information
		locationService = new LocationService(interpreter);
		mWebView.addJavascriptInterface(locationService, "locationService");
		
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
			Log.i(TAG, "creation: " + msg);
			interpreter.eval(msg);
		} else {
			// Assume message is for current station
			interpreter.eval(String.format("station.onMessage('%s', '%s');", type, msg)); 
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
