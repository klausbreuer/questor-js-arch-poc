package de.questor.poc.jsarch;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import de.questor.poc.jsarch.simulator.Runtime;

public class QuestorJsArchPocActivity extends Activity {

	Renderer mRenderer ;
	
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
		
		mRenderer = new Renderer(this);
		
	}


	/** Questor class which is accessible from server generated Javascript code */
	class Questor {
		private WebView wv;
		
		private Runtime r;
		
		public Questor(WebView wv) {
			this.wv = wv;
		}
		
		public void becomeRenderer() {
			Log.i("questor", "renderer");
			
			//mRenderer.onMessage("alert('buh!');showToast('hihi');");
			//mRenderer.onMessage("var q = new Renderer.QuizStation ('wie hiess die tarent frueher, als alles noch viel frueher war?'); q.onSubmit('cic');");
			//mRenderer.onMessage("var q = new Renderer.QuizStation ('wie hiess die tarent frueher, als alles noch viel frueher war?');");
			mRenderer.onMessage("var q = new Renderer.QuizStationHtml (); q.setQuestion('wie spaet ist es?'); q.setButtonText('und los gehts....'); q.show();");
			
		}

		public void becomeSimulator() {
			Log.i("questor", "simulator");
			WebView wv2 = new WebView(wv.getContext());
			r = new Runtime(wv2, wv.getContext().getAssets());
		}

		public void exit() {
			System.exit(0);
		}
		
		public void test() {
			r.run();
		}
		
	}
}