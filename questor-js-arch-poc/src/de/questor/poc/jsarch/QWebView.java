package de.questor.poc.jsarch;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

public class QWebView extends WebView {
	
	String weinreLink = "";
	String weinreLinkInScriptTag  = "";
	String addWeinreScript = ";";  

	public QWebView(Context context) {
		super(context);
		//weinreLink = "http://192.168.2.103:8080/target/target-script-min.js#anonymous";
		weinreLink = context.getString(R.string.weinreLink);
		
		if (!("".equals(weinreLink))) {
			Log.i("klaus", "soso");
			weinreLinkInScriptTag = "<script src='" + weinreLink + "'></script>";
			addWeinreScript = 
					" var snode = document.createElement('script');" +  
					" snode.setAttribute('type','text/javascript');" +  
					" snode.setAttribute('src','" + weinreLink + "');" +   
					" var head = document.getElementsByTagName('head')[0];" +  
					" if (head) head.appendChild(snode);";  
		}
	}

	
	@Override
	public void loadData(String data, String mimeType, String encoding) {
		super.loadData(weinreLinkInScriptTag + data, mimeType, encoding);
	}
	
	
	@Override
	public void loadUrl(String url) {
		super.loadUrl(url);
		// Das hier klappt noch nicht richtig: wenn dasselbe webview zum zweiten mal loadUrl aufruft, würde weinre noch mal includiert...
		// mit der Folge, dass es nicht mehr funktioniert...
		//super.loadUrl("javascript:" + addWeinreScript);		
	}
	
}
