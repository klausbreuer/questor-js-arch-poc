/** Session class
 * Holds the player and his/her current station.
 */
Session = function(playerId) {
	this.playerId = playerId;
}

/** Simulator class
 * Keeps track of the stations and the sessions.
 */
Simulator = function() {
	this.sessions = new Object();
	this.stations = new Object();
	
	// What follows is an actual 'game' configuration.
	this.stations["1st_question"] = new QuizStation(
				"Wie hiess die tarent frueher, als alles noch viel frueher war?",
<<<<<<< HEAD
				"Antworten", "cic", "1st_compass", "fail");
	

	this.stations["1st_compass"] = new CompassStation("2nd_question", "fail");
	
=======
				"Antworten", "cic", "2nd_question", "fail");
>>>>>>> - implemented the htmlstation on the simulator side
	
	this.stations["2nd_question"] = new QuizStation(
				"5 + 5 = ?",
				"Antworten", "10", "1st_html", "1st_question");
	
	this.stations["1st_html"] = new HtmlStation(
				"<p>At the foot of the hill, the path splits into two directions, " +
				"both leading into a large wood. " +
				"You can take " +
				"the <choice target=\"1st_question\">right</choice> " +
				"or <choice target=\"2nd_question\">left</choice> " +
				"or <choice target=\"1st_html\">up</choice> " +
				"or <choice target=\"2nd_html\">down</choice> track into the wood.</p>");

	this.stations["2nd_html"] = new HtmlStation(
				"<p>Hey!! Super! Du hast den Ausgang gefunden!!! " +
				"<br><choice target=\"success\">Hier</choice> gehts raus...</p>");

	this.stations["success"] = new EndStation(0);
	this.stations["fail"] = new EndStation(1);
	
	this.start = "1st_question";
};

/** This method handles the messages send from the renderer.
*/	
Simulator.prototype.onMessage = function(type, ctx, msg) {
	if ("join" == type) {
		session = this.newSession(msg);
		this.performTransition(session, this.start);
	} else if ("reply" == type) {
		s = this.toSession(ctx);
		s.station.onMessage(s, msg);
	} else {
		logger.i("Unexpected message type: " + type);
	}
};

Simulator.prototype.sendCreateStation = function(session, msg) {
		runtime.sendToRenderer("create", this.toContext(session), msg);
};


Simulator.prototype.sendMessage = function(type, session , msg) {
	runtime.sendToRenderer(type, this.toContext(session), msg);
};



Simulator.prototype.performTransition = function(session, newStationId) {
	session.station = this.stations[newStationId];
	session.stationId = newStationId;
	
	// TODO: Do this as a task in a general task queue
	session.station.onEnter(session);
};

Simulator.prototype.newSession = function(playerId) {
	s = new Session(playerId);
	this.sessions[playerId] = s;
	return s;
};

Simulator.prototype.toContext = function(session) {
	return session.playerId;
};

Simulator.prototype.toSession = function(context) {
	return this.sessions[context];
};
	

