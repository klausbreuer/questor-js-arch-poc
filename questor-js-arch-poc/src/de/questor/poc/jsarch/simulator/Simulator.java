package de.questor.poc.jsarch.simulator;

import java.util.HashMap;

import android.util.Log;
import de.questor.poc.jsarch.MessageService;

public class Simulator {
	
	private static String TAG = "Simulator";

	HashMap<String, QuizStation> stations = new HashMap<String, QuizStation>();
	
	MessageService messageService;
	
	public Simulator() {
		// TODO: This needs to be loaded from somewhere
		stations.put("start", new QuizStation(this,
				"Wie hiess die tarent frueher, als alles noch viel frueher war?",
				"Antworten", "cic", "success", "fail"));
	}
	
	public void onMessage(String type, Object ctx, String msg) {
		if ("join".equals(type)) {
			Session session = new Session(msg); 
			performTransition(session, "start");
		} else if ("reply".equals("type")) {
			// This is a placeholder operation of how the publicly known context
			// object can be internally resolved into something the Simulator knows
			// how to access a player and her current station.
			Session s = (Session) ctx;
			
			s.station.onMessage(s, msg);
		} else {
			Log.w(TAG, "Unexpected message type: " + type);
		}
	}

	void sendCreateStation(Session session, String msg) {
		messageService.sendToRenderer(
				"create",
				(Object) session,
				msg);
	}

	void performTransition(Session session, String newStation) {

		if ("fail".equals(newStation)) {
			Log.i(TAG, "fail");
			System.exit(1);
		} else if ("success".equals(newStation)) {
			Log.i(TAG, "success");
			System.exit(0);
		}
		
		session.station = stations.get(newStation);

		// TODO: Do this as a task in a general task queue
		session.station.onEnter(session);
	}
	
	static class Session {
		String playerId;
		QuizStation station;

		Session(String playerId) {
			this.playerId = playerId;
		}
		
	}

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

}
