package de.questor.poc.jsarch;

import android.content.Context;
import android.webkit.WebView;

public class QWebView extends WebView {
	
	String weinreLink = null;
	String weinreLinkInScriptTag  = "";
	String addWeinreScript = ";";  

	public QWebView(Context context) {
		super(context);
		//weinreLink = "http://192.168.2.103:8080/target/target-script-min.js#anonymous";
		weinreLink = context.getString(R.string.weinreLink);
		
		if (weinreLink != null) {
			weinreLinkInScriptTag = "<script src='" + weinreLink + "'></script>";
			addWeinreScript = 
					" var snode = document.createElement('script');" +  
					" snode.setAttribute('type','text/javascript');" +  
					" snode.setAttribute('src','" + weinreLink + "');" +   
					" document.getElementsByTagName('head')[0].appendChild(snode);";  
		}
	}

	
	@Override
	public void loadData(String data, String mimeType, String encoding) {
		super.loadData(weinreLinkInScriptTag + data, mimeType, encoding);
	}
	
	
	@Override
	public void loadUrl(String url) {
		super.loadUrl(url);
		super.loadUrl("javascript:" + addWeinreScript);		
	}
	
}
