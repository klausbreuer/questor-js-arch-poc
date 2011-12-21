package de.questor.poc.jsarch;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class QuestorJsArchPocActivity extends Activity {

	Renderer mRenderer ;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		WebView wv = (WebView) findViewById(R.id.webview);

		wv.getSettings().setJavaScriptEnabled(true);
		wv.addJavascriptInterface(new Questor(), "questor");

		wv.loadUrl("file:///android_asset/main.html");
		
		mRenderer = new Renderer(this);
		
	}


	/** Questor class which is accessible from server generated Javascript code */
	public class Questor {
		public void becomeRenderer() {
			Log.i("questor", "renderer");
			
			//mRenderer.onMessage("alert('buh!');showToast('hihi');");
			//mRenderer.onMessage("var q = new Renderer.QuizStation ('wie hiess die tarent frueher, als alles noch viel frueher war?'); q.onSubmit('cic');");
			mRenderer.onMessage("var q = new Renderer.QuizStation ('wie hiess die tarent frueher, als alles noch viel frueher war?');");
			
		}

		public void becomeSimulator() {
			Log.i("questor", "simulator");
		}

		public void exit() {
			System.exit(0);
		}
	}
}