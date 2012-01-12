/** Session class
 * Holds the player and his/her current station.
 */
Session = function(sessionId, playerId) {
	this.sessionId = sessionId;
	this.playerId = playerId;
}

/** Simulator class
 * Keeps track of the stations and the sessions.
 */
Simulator = function() {
	this.sessions = new Object();
	this.stations = null;
};

Simulator.prototype.setGame = function(start, stations) {
	this.start = start;
	this.stations = stations;
}

/** This method handles the messages send from the renderer.
*/	
Simulator.prototype.onMessage = function(ctx, msg) {
	logger.i("Simulator.onMessage('{0}', '{1}'}".format(ctx, msg));
	
	var msgObj = null;
	try {
		msgObj = JSON.parse(msg);
	} catch (e) {
		logger.e("Unable to parse message: " + e);
		return;
	}
	
	switch (msgObj.type) {
		case 'join':
			var session = this.newSession(ctx, msgObj.playerId);
			this.performTransition(session, this.start);
			break;
		case 'reply':
			var s = this.toSession(ctx);
			if (s == null) {
				logger.e("Could not find session for context: " + ctx);
				return;
			};
			
			s.station.onMessage(s, msgObj.data);
			break;
		default:
			logger.i("Unexpected message type: " + msgObj.type);
			break;
	}
	
};

Simulator.prototype.sendCreateMessage = function(session, stationClass, data) {
	var obj = {
			type: "create",
			stationClass: stationClass,
			data: data
	};

	this.sendMessageObject(session, obj);
};

Simulator.prototype.sendStationMessage = function(session, data) {
	var obj = {
		type: "station",
		data: data
	};
	
	this.sendMessageObject(session, obj);
};

/** Sends a fully prepared message object. The caller is supposed to set
 * the 'type' field which has to be understood by the Renderer instance.
 */
Simulator.prototype.sendMessageObject = function(session, obj) {
	var newMsg = JSON.stringify(obj);
	
	runtime.sendToRenderer(this.toContext(session), newMsg);
};

Simulator.prototype.performTransition = function(session, newStationId) {
	session.station = this.stations[newStationId];
	session.stationId = newStationId;
	
	// TODO: Do this as a task in a general task queue
	session.station.onEnter(session);
};

Simulator.prototype.newSession = function(sessionId, playerId) {
	s = new Session(sessionId, playerId);
	this.sessions[sessionId] = s;
	return s;
};

Simulator.prototype.toContext = function(session) {
	return session.sessionId;
};

Simulator.prototype.toSession = function(context) {
	return this.sessions[context];
};
	

