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

	private Runnable runnable;

	QuizStation(Context ctx, Simulator sim, final String question, final String buttonText,
			final String answer, final String stationSuccess, final String stationFail) {
		this.sim = sim;

		wv = new WebView(ctx);

		wv.getSettings().setJavaScriptEnabled(true);
		wv.setWebChromeClient(new WebChromeClient());
		wv.addJavascriptInterface(new Logger("QuizStation"), "logger");
		wv.addJavascriptInterface(new Callback(), "sim");

		wv.loadUrl("file:///android_asset/simulator/simulator.html");
		
		// Initializes a station instance.
		runnable = new Runnable() {
			public void run() {
				wv.loadUrl(String
						.format("javascript:(function() { var q = new QuizStation('%s', '%s', '%s', '%s', '%s'); })()",
								question, buttonText, answer, stationSuccess,
								stationFail));
			}
		};
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

		public void sendCreateStation(String msg) {
			sim.sendCreateStation(session, msg);
		}

		public void performTransition(Session session, String newStation) {
			sim.performTransition(session, newStation);
		}

		/**
		 * This method is implicitly called when parsing of the QuizStation code is
		 * finished. After this any Javascript URLs can be called.
		 */
		public void finished() {
			runnable.run();
		}
	}

}
