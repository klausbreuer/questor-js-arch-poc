package de.questor.poc.jsarch.simulator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.res.AssetManager;
import android.util.Log;
import android.webkit.WebView;

public class SimulatorRuntime {

	private WebView wv;

	public SimulatorRuntime(WebView wv, AssetManager am) {
		this.wv = wv;

		wv.getSettings().setJavaScriptEnabled(true);
		wv.addJavascriptInterface(new Logger("simulator"), "logger");

		wv.loadUrl("file:///android_asset/simulator/simulator.html");
	}
	
	/**
	 * Loads
	 * 
	 * @param url
	 */
	public void loadJavascript(AssetManager am, String res) {
		String jscontent = "";
		try {
			InputStream is = am.open(res);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String line;
			while ((line = br.readLine()) != null) {
				jscontent += line;
			}
			is.close();
		} catch (Exception e) {
		}
		Log.i("jscontent", "jscontent: " + jscontent);
		
		wv.loadUrl("javascript:createNewScript(" + jscontent + ");");
	}

	public void run() {
	}

}
