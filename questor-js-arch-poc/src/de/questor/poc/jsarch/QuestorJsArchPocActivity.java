package de.questor.poc.jsarch;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
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


	/** Questor class which is accessible from Javascript code */
	class Questor {
		private WebView wv;
		
		SimulatorRuntime simulator = new SimulatorRuntime(QuestorJsArchPocActivity.this);
		Renderer mRenderer = new Renderer(QuestorJsArchPocActivity.this);
		
		public Questor(WebView wv) {
			this.wv = wv;
		}
		
		public void testKlaus() {
			Log.i("questor", "testKlaus");
			
			//mRenderer.onMessage("create", null, "alert('buh!');showToast('hihi');");
			//mRenderer.onMessage("create", null, "var q = new Renderer.QuizStation ('wie hiess die tarent frueher, als alles noch viel frueher war?'); q.onSubmit('cic');");
			//mRenderer.onMessage("create", null, "var q = new Renderer.QuizStation ('wie hiess die tarent frueher, als alles noch viel frueher war?');");
			//mRenderer.onMessage("create", null, "var q = new Renderer.QuizStationHtml (); q.setQuestion('wie spaet ist es?'); q.setButtonText('und los gehts....'); q.show();");
			//mRenderer.onMessage("create", null, "var q = new Renderer.QuizStationHtml (); q.setQuestion('QuizStationHtml: wie spaet ist es?'); q.setButtonText('push me!'); q.show();");
			mRenderer.onMessage("create", null, "var q = new Renderer.HtmlStation (); q.setContent('<p>At the foot of the hill, the path splits into two directions, both leading into a large wood. You can take the <choice target=\"85\">right</choice> or <choice target=\"999\">left</choice> track into the wood.</p>'); q.show();");			
			
		}


		public void exit() {
			System.exit(0);
		}
		
		public void test() {
			// Initializes a local message service and runs a game.
			MessageService ms = new LocalMessageService(simulator, mRenderer);
			mRenderer.joinTest();
		}
		
	}
}
