package de.questor.poc.jsarch.simulator;

import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import de.questor.poc.jsarch.Logger;
import de.questor.poc.jsarch.simulator.Simulator.Session;

/**
 * Server sided QuizStation.
 * 
 * TODO: To be implemented in Javascript at some point.
 * 
 * @author rschus
 * 
 */
public class QuizStation {

	private Simulator sim;

	private Session session;

	private WebView wv;

	QuizStation(Context ctx, Simulator sim, String question, String buttonText,
			String answer, String stationSuccess, String stationFail) {
		this.sim = sim;

		wv = new WebView(ctx);

		wv.getSettings().setJavaScriptEnabled(true);
		wv.setWebChromeClient(new WebChromeClient());
		wv.addJavascriptInterface(new Logger("QuizStation"), "logger");
		wv.addJavascriptInterface(new Callback(), "sim");

		wv.loadUrl("file:///android_asset/simulator/simulator.html");
		wv.loadUrl("javascript:(function() { test(); })()");
		
		wv.loadUrl(String
				.format("javascript:(function() { var q = new QuizStation('%s', '%s', '%s', '%s', '%s'); })()",
						question, buttonText, answer, stationSuccess,
						stationFail));
	}

	public void onEnter(Simulator.Session session) {
		this.session = session;
		wv.loadUrl("javascript:(function() { station.onEnter(); })()");
	}

	public void onMessage(Simulator.Session session, String msg) {
		wv.loadUrl(String.format(
				"javascript:(function() { station.onMessage('%s'); })()", msg));
	}

	class Callback {

		void sendCreateStation(String msg) {
			sim.sendCreateStation(session, msg);
		}

		void performTransition(Session session, String newStation) {
			sim.performTransition(session, newStation);
		}
	}

}
