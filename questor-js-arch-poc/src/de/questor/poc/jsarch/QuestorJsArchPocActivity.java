package de.questor.poc.jsarch;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import de.questor.poc.jsarch.simulator.SimulatorRuntime;
import de.questor.poc.jsarch.simulator.SimulatorRuntime;

public class QuestorJsArchPocActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		WebView wv = (WebView) findViewById(R.id.webview);

		wv.getSettings().setJavaScriptEnabled(true);
		wv.setWebChromeClient(new WebChromeClient());
		
		wv.addJavascriptInterface(new Questor(wv), "questor");
		
		wv.loadUrl("file:///android_asset/main.html");
		
	}


	/** Questor class which is accessible from server generated Javascript code */
	class Questor {
		private WebView wv;
		
		SimulatorRuntime simulator = new SimulatorRuntime(QuestorJsArchPocActivity.this);
		Renderer mRenderer = new Renderer(QuestorJsArchPocActivity.this);
		
		public Questor(WebView wv) {
			this.wv = wv;
		}
		
		public void exit() {
			System.exit(0);
		}
		
		public void test() {
			// Initializes a local message service and runs a game.
			MessageService ms = new MessageService(simulator, mRenderer);
			mRenderer.joinTest();
		}
		
	}
}