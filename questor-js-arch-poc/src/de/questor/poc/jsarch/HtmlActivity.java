package de.questor.poc.jsarch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class HtmlActivity extends Activity {

	private WebView mWebView;
	private String mContent;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Get the content from the intent:
		Intent i = getIntent();
		mContent = (String) i.getSerializableExtra("content");
		
		mWebView = new WebView(this);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.addJavascriptInterface(new Callback(), "callback");
		mWebView.loadData(mContent, "text/html", null);
		
		setContentView(mWebView);
		
		Log.i("klaus", mContent);

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	class Callback {
		public void onSubmit(String value) {
			Renderer.INSTANCE.stationOnSubmit(value);
		}
	}

}
