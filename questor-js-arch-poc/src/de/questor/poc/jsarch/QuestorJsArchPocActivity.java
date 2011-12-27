package de.questor.poc.jsarch;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import de.questor.poc.jsarch.simulator.Simulator;
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
		
		Simulator simulator = new Simulator(QuestorJsArchPocActivity.this);
		Renderer mRenderer = new Renderer(QuestorJsArchPocActivity.this);
		
		public Questor(WebView wv) {
			this.wv = wv;
		}
		
		public void becomeRenderer() {
			Log.i("questor", "renderer");
			
			//mRenderer.onMessage("create", null, "alert('buh!');showToast('hihi');");
			//mRenderer.onMessage("create", null, "var q = new Renderer.QuizStation ('wie hiess die tarent frueher, als alles noch viel frueher war?'); q.onSubmit('cic');");
			//mRenderer.onMessage("create", null, "var q = new Renderer.QuizStation ('wie hiess die tarent frueher, als alles noch viel frueher war?');");
			//mRenderer.onMessage("create", null, "var q = new Renderer.QuizStationHtml (); q.setQuestion('wie spaet ist es?'); q.setButtonText('und los gehts....'); q.show();");
			mRenderer.onMessage("create", null, "var q = new Renderer.QuizStationHtml (); q.setQuestion('QuizStationHtml2: wie spaet ist es?'); q.setButtonText('push me!'); q.show();");
			
		}

		public void becomeSimulator() {
			Log.i("questor", "simulator");
		}

		public void exit() {
			System.exit(0);
		}
		
		public void test() {
			MessageService ms = new MessageService(simulator, mRenderer);
			mRenderer.joinTest();
		}
		
	}
}
