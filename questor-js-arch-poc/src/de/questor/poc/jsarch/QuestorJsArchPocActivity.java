package de.questor.poc.jsarch;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class QuestorJsArchPocActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		WebView wv = (WebView) findViewById(R.id.webview);

		wv.getSettings().setJavaScriptEnabled(true);
		wv.addJavascriptInterface(new Questor(), "questor");

		wv.loadUrl("file:///android_asset/quizstation-static.html");
	}

	/** Questor class which is accessible from server generated Javascript code */
	static class Questor {
		public void test(String msg) {
			Log.i("questor", msg);
		}

		public void exit() {
			System.exit(0);
		}
	}
}