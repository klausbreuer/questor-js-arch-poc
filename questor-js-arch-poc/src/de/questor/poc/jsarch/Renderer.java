package de.questor.poc.jsarch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

public class Renderer {

	private static final int SHOW_STORY_NODE_REQUEST = 0;

	Context mContext;
	WebView mWebView;
	String rendererJsLib;

	public Renderer(Context pContext) {

		mContext = pContext;
		mWebView = new WebView(mContext);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.addJavascriptInterface(new JavaScriptRuntimeBridge(), "runtime");

		// we get the renderer lib from the assets:
		BufferedReader r;
		try {
			r = new BufferedReader(new InputStreamReader(mContext.getAssets().open("renderer.js")));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
			}
			rendererJsLib = total.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Log.i("klaus", rendererJsLib);

	}

	public void onMessage(String pMessage) {

		String command = rendererJsLib + "<script>" + pMessage + "</script>";
		mWebView.loadData(command, "text/html", null);

		// Log.i("klaus", "onMessage: " + pMessage + " Command: " + command);

	}

	public class JavaScriptRuntimeBridge {

		public JavaScriptRuntimeBridge() {
		}

		public void showToast(String toast) {
			Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();

		}

		public void showQuizStation(String pQuestion) {
			Intent i = new Intent(mContext, QuizActivity.class);
			i.putExtra("question", pQuestion);
			//((Activity)mContext).startActivityForResult(i, SHOW_STORY_NODE_REQUEST);
			mContext.startActivity(i);
		}
		
		public void showHtmlStation(String pContent) {
			Log.i("klaus", "showHTMLStation");

			Intent i = new Intent(mContext, HtmlActivity.class);
			i.putExtra("content", pContent);
			mContext.startActivity(i);
		}
		

	}

}
