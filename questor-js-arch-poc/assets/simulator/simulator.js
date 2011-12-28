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
	
	// What follows is an actual 'game' configuration.
	this.stations["1st_question"] = new QuizStation(
				"Wie hiess die tarent frueher, als alles noch viel frueher war?",
				"Antworten", "cic", "2nd_question", "fail");
	this.stations["2nd_question"] = new QuizStation(
				"5 + 5 = ?",
				"Antworten", "10", "success", "1st_question");
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

EndStation = function(returnCode) {
	this.returnCode = returnCode;
};

EndStation.prototype.onEnter = function(session) {
	logger.i("endstation: " + session.stationId);
	runtime.exit(this.returnCode);
};

runtime.finished();