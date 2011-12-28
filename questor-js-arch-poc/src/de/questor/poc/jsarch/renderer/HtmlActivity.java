package de.questor.poc.jsarch.renderer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import de.questor.poc.jsarch.QWebView;

public class HtmlActivity extends Activity {

	private QWebView mWebView;
	private String mContent;
	private RendererRuntime rendererRuntime;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Get the html content from the intent:
		Intent i = getIntent();
		mContent = (String) i.getSerializableExtra("content");
		
		rendererRuntime = RendererRuntime.getInstance();
		rendererRuntime.setContext(this);
		
		mWebView = new QWebView(this);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.addJavascriptInterface(rendererRuntime, "runtime");
		mWebView.loadData(mContent, "text/html", null);
		
		setContentView(mWebView);
		
		//Log.i("klaus", mContent);

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
	   return;
	}	

}
