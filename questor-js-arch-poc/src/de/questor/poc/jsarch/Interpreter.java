package de.questor.poc.jsarch;

public class Interpreter {

	private QWebView webView;
	
	public Interpreter(QWebView webView) {
		this.webView = webView;
	}
	
	public void eval(String javaScript) {
		webView.loadUrl("javascript:(function() { " + javaScript + "})()");
	}
	
}
