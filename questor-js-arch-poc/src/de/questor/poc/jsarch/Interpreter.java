package de.questor.poc.jsarch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.webkit.WebView;

public class Interpreter {

	private WebView webView;

	public Interpreter(WebView webView) {
		this.webView = webView;
	}

	public void eval(String javaScript) {
		webView.loadUrl("javascript:(function() { " + javaScript + "})()");
	}

	public void eval(InputStream is) {
		BufferedReader r = new BufferedReader(new InputStreamReader(is));

		String l = null;
		StringBuilder sb = new StringBuilder();
		try {
			while ((l = r.readLine()) != null) {
				sb.append(l);
				sb.append("\n");
			}
		} catch (IOException ioe) {
			throw new IllegalStateException(ioe);
		} finally {
			try {
				is.close();
			} catch (IOException ioe) { }
		}

		eval(sb.toString());
	}

}
