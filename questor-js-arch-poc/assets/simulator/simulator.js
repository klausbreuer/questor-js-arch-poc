var simulator;
var station;

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
	this.stations["start"] = new QuizStation(
				"Wie hiess die tarent frueher, als alles noch viel frueher war?",
				"Antworten", "cic", "next", "fail");
	this.stations["next"] = new QuizStation(
				"5 + 5 = ?",
				"Antworten", "10", "success", "start");
};
	
Simulator.prototype.onMessage = function(type, ctx, msg) {
	if ("join" == type) {
		session = this.newSession(msg);
		this.performTransition(session, "start");
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

Simulator.prototype.performTransition = function(session, newStation) {
	if ("fail" == newStation) {
		logger.i("fail");
		runtime.exit(1);
	} else if ("success" == newStation) {
		logger.i("success");
		runtime.exit(0);
	}
	
	session.station = this.stations[newStation];
	
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
	
QuizStation = function(pQuestion, pButtonText, pAnswer, pStationSuccess, pStationFail) {
	station = this;
	this.question = pQuestion;
	this.buttonText = pButtonText;
	this.answer = pAnswer;
	this.stationSuccess = pStationSuccess;
	this.stationFail = pStationFail;
};

QuizStation.prototype.onEnter = function(session) {
	simulator.sendCreateStation(session, this.generateJavascript());
};

QuizStation.prototype.onMessage = function(session, msg) {
	if (this.answer == msg) {
		simulator.performTransition(session, this.stationSuccess);
	} else {
		simulator.performTransition(session, this.stationFail);
	}
};

QuizStation.prototype.generateJavascript = function() {
	var generatorCode = 
		("var q = new Renderer.QuizStationHtml ();"
		+ "q.setQuestion('{0}'); "
		+ "q.setButtonText('{1}'); "
		+ "q.show();").format(this.question, this.buttonText);
	return generatorCode;
};

runtime.finished();