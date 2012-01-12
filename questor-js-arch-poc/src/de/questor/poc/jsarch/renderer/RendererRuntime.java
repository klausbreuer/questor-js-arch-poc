package de.questor.poc.jsarch.renderer;

import java.util.Iterator;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.webkit.WebChromeClient;
import de.questor.poc.jsarch.Interpreter;
import de.questor.poc.jsarch.Logger;
import de.questor.poc.jsarch.MessageService;
import de.questor.poc.jsarch.QWebView;
import de.questor.poc.jsarch.QuestorContext;
import de.questor.poc.jsarch.renderer.compass.CompassDelegate;
import de.questor.poc.jsarch.renderer.html.HtmlDelegate;
import de.questor.poc.jsarch.renderer.quiz.QuizDelegate;

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
	
	private Runnable runnable;
	
	public RendererRuntime(Context pContext) {
		INSTANCE = this;
		mContext = pContext;
		
		// Implementation note: This would be done elsewhere through a central
		// registry file or whatever
		delegateManager = new DelegateManager();
		delegateManager.registerDelegate("compassDelegate", new CompassDelegate(mContext));
		delegateManager.registerDelegate("htmlDelegate", new HtmlDelegate(mContext));
		delegateManager.registerDelegate("quizDelegate", new QuizDelegate(mContext));
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
		
		// Initializes the global simulator instance in Javascript.
		runnable = new Runnable() {
			public void run() {
				interpreter.eval("renderer = new Renderer();");
				
				// Makes sure that everything has been initialized correctly.
				interpreter.eval("checkRenderer();");
				
				runnable = null;
			}
		};

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
	
	public void onMessage(QuestorContext ctx, String msg) {
		mQuestorContext = ctx;

		// msg is not supposed to contain ' (single quote) chars otherwise
		// the call is not going to work.
		if (msg.contains("'")) {
			throw new IllegalStateException("Message contains single-quotes. You need to fix that!");
		}
		
		interpreter.eval(String.format("renderer.onMessage('%s');", msg));
	}

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

	/**
	 * A messaging function that is called from Javascript which allows sending
	 * a reply to the simulator.
	 * 
	 * @param msg
	 */
	public void sendReplyInternal(String msg) {
		if (mQuestorContext == null) {
			Log.i(TAG, "error: mQuestorContext is null!");
		}
		else {
			mQuestorContext.sendMessage(msg);
		}
	}
	
	public void sendReply(String msg) {
		// msg is not supposed to contain ' (single quote) chars otherwise
		// the call is not going to work.
		if (msg.contains("'")) {
			throw new IllegalStateException("Message contains single-quotes. You need to fix that!");
		}
		
		// Route the data through the renderer.
		interpreter.eval(String.format("renderer.sendReply('%s')", msg));
	}

	public void finished() {
		runnable.run();
		runnable = null;
	}
}
