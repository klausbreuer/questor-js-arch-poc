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
};

Simulator.prototype.setGame = function(start, stations) {
	this.start = start;
	this.stations = stations;
}

/** This method handles the messages send from the renderer.
*/	
Simulator.prototype.onMessage = function(type, ctx, msg) {
	logger.i("Simulator.onMessage('{0}', '{1}', '{2}'}".format(type, ctx, msg));
	
	if ("join" == type) {
		var session = this.newSession(msg);
		this.performTransition(session, this.start);
	} else if ("reply" == type) {
		var s = this.toSession(ctx);
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
	

