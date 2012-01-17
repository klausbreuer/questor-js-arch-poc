package de.questor.poc.jsarch;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import de.questor.poc.jsarch.net.RemoteMessageServiceClient;
import de.questor.poc.jsarch.net.RemoteMessageServiceServer;
import de.questor.poc.jsarch.renderer.RendererRuntime;
import de.questor.poc.jsarch.simulator.SimulatorRuntime;

public class QuestorJsArchPocActivity extends Activity {

	SimulatorRuntime simulator;
	RendererRuntime mRenderer;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		WebView wv = (WebView) findViewById(R.id.webview);

		wv.getSettings().setJavaScriptEnabled(true);
		wv.setWebChromeClient(new WebChromeClient());
		
		wv.addJavascriptInterface(new Logger("main.html"), "logger");
		wv.addJavascriptInterface(new Questor(wv), "questor");
		
		wv.loadUrl("file:///android_asset/main.html");
		
		simulator = new SimulatorRuntime(this);
		mRenderer = new RendererRuntime(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mRenderer.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mRenderer.onResume();
	}

	/** Questor class which is accessible from Javascript code */
	class Questor {
		private WebView wv;
		
		public Questor(WebView wv) {
			this.wv = wv;
		}
		
		public void testKlaus() {
			Log.i("questor", "testKlaus");
			
			// TODO: If this is still needed it needs to be converted to JSON syntax.
			//mRenderer.onMessage("create", null, "alert('buh!');showToast('hihi');");
			//mRenderer.onMessage("create", null, "var q = new Renderer.QuizStation ('wie hiess die tarent frueher, als alles noch viel frueher war?'); q.onSubmit('cic');");
			//mRenderer.onMessage("create", null, "var q = new Renderer.QuizStation ('wie hiess die tarent frueher, als alles noch viel frueher war?');");
			//mRenderer.onMessage("create", null, "var q = new Renderer.QuizStationHtml (); q.setQuestion('wie spaet ist es?'); q.setButtonText('und los gehts....'); q.show();");
			//mRenderer.onMessage("create", null, "var q = new Renderer.QuizStationHtml (); q.setQuestion('QuizStationHtml: wie spaet ist es?'); q.setButtonText('push me!'); q.show();");
			//mRenderer.onMessage(null, "var q = new Renderer.HtmlStation (); q.setContent('<p>At the foot of the hill, the path splits into two directions, both leading into a large wood. You can take the <choice target=\"20\">right</choice> or <choice target=\"30\">left</choice> or <choice target=\"40\">up</choice> or <choice target=\"50\">down</choice> track into the wood.</p>'); q.show();");			
			
		}


		public void exit() {
			System.exit(0);
		}

		/*
		public void test() {
			// Initializes a local message service and runs a game.
			MessageService ms = new LocalMessageService(simulator, mRenderer);
			
			// Starts a game by letting a player join the game ... 
			ms.sendToSimulator("testspieler", "{ \"type\":\"join\", \"playerId\":\"testspieler\" }");
		}
		*/

		public void test() {
			RemoteMessageServiceServer rmss = new RemoteMessageServiceServer(simulator, null, 15000);
			simulator.setMessageService(rmss);
			
			RemoteMessageServiceClient.Runnable r = new RemoteMessageServiceClient.Runnable() {
				public void run(RemoteMessageServiceClient that) {
					// Starts a game by letting a player join the game ... 
					mRenderer.join("testspieler");
				}
			};
			
			RemoteMessageServiceClient rmsc = new RemoteMessageServiceClient(mRenderer, r, "localhost", 15000);
			mRenderer.setMessageService(rmsc);
		}
		
		public void netTest(String hostString, String portString, final String playerIdString) {
			int port = Integer.parseInt(portString);
			
			RemoteMessageServiceClient.Runnable r = new RemoteMessageServiceClient.Runnable() {
				public void run(RemoteMessageServiceClient that) {
					mRenderer.join(playerIdString);
				}
			};
			
			RemoteMessageServiceClient rmsc = new RemoteMessageServiceClient(mRenderer, r, hostString, port);
			mRenderer.setMessageService(rmsc);
		}
	}
}
